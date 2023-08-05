package com.example.restaurant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import Adapter.CategoryAdapter;
import Adapter.PopularAdapter;
import Helper.ManagementCart;
import Models.CategoryModel;
import Models.FoodModel;


public class HomeFragment extends Fragment implements OnCategoryClickListener{

    private FirebaseAuth firebaseAuth;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    TextView tdeals;


    FirestoreRecyclerAdapter<FoodModel, HomeFragment.FoodViewHolder> noteAdapter;

    private RecyclerView.Adapter adapter,adapter2;
    TextView nitemcart;
    private ImageView icart;
    private RecyclerView rvcategories,rvpopular;
    private ArrayList<FoodModel> cartItems;

    public HomeFragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootview=inflater.inflate(R.layout.fragment_home, container, false);
        rvcategories=rootview.findViewById(R.id.rvcategories);
        rvpopular=rootview.findViewById(R.id.rvpopular);
        icart=rootview.findViewById(R.id.icart);
        tdeals=rootview.findViewById(R.id.tdeals);
        icart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(requireContext(), CartActivity.class);
                startActivity(i);
            }
        });

        tdeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              replaceFragment(new MenuFragment(),"menu_fragment");
            }
        });

        nitemcart=rootview.findViewById(R.id.nitemcart);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        rvpopular.setLayoutManager(linearLayoutManager);

        getCartItems();
        recyclerViewCategory();
        recyclerViewPopular();

        return rootview;
    }



    private void recyclerViewCategory() {

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);

        rvcategories.setLayoutManager(linearLayoutManager);
        ArrayList<CategoryModel> category=new ArrayList<>();
        category.add(new CategoryModel("Pizza","pizza"));
        category.add(new CategoryModel("Burger","hamburger"));
        category.add(new CategoryModel("Pasta","pasta"));
        category.add(new CategoryModel("Donut","donut"));
        category.add(new CategoryModel("Drink","drink"));

        adapter=new CategoryAdapter(category);

        rvcategories.setAdapter(adapter);



    }



    @Override
    public void onCategoryClick(int position) {
        if (position == 0) {
            Intent intent = new Intent(getContext(), PizzaActivity.class);
            startActivity(intent);
        }
        else if (position == 1) {
            Intent intent = new Intent(getContext(), BurgerActivity2.class);
            startActivity(intent);
        }
        else if (position == 2) {
            Intent intent = new Intent(getContext(), PastaActivity2.class);
            startActivity(intent);
        }
        else if (position == 3) {
            Intent intent = new Intent(getContext(), DonutActivity2.class);
            startActivity(intent);
        }
        else if (position == 4) {
            Intent intent = new Intent(getContext(), DrinkActivity2.class);
            startActivity(intent);
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ArrayList<CategoryModel> category=new ArrayList<>();
        CategoryAdapter categoryAdapter = new CategoryAdapter(category);
        category.add(new CategoryModel("Pizza","pizza"));
        category.add(new CategoryModel("Burger","hamburger"));
        category.add(new CategoryModel("Pasta","pasta"));
        category.add(new CategoryModel("Donut","donut"));
        category.add(new CategoryModel("Drink","drink"));
        categoryAdapter.setOnCategoryClickListener(this);
        rvcategories.setAdapter(categoryAdapter);


    }

    private void getCartItems() {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        cartItems = new ArrayList<>();

        firebaseFirestore.collection("Cart")
                .document(currentUserUid)
                .collection("Items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    cartItems.clear();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        FoodModel cartItem = documentSnapshot.toObject(FoodModel.class);
                        cartItems.add(cartItem);
                    }
                    updateCartItemCount();
                })
                .addOnFailureListener(e -> {

                });
    }

    private void updateCartItemCount() {
        int totalItems = 0;
        for (FoodModel cartItem : cartItems) {
            totalItems += cartItem.getNumberInCart();
        }
        nitemcart.setText(String.valueOf(totalItems));
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder {

        private TextView tpopulartitle, tpopularprice, tpopularadd;
        ImageView ipopularimg;
        LinearLayout pizzalayout;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            tpopulartitle = itemView.findViewById(R.id.tpopulartitle);
            tpopularprice = itemView.findViewById(R.id.tpopularprice);
            tpopularadd = itemView.findViewById(R.id.tpopularadd);
            ipopularimg = itemView.findViewById(R.id.ipopularimg);
            pizzalayout = itemView.findViewById(R.id.pizzalayout);

            tpopularadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tpopularadd.setClickable(false);
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        FoodModel foodItem = noteAdapter.getItem(position);
                        addToCart(foodItem);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Item Added to Cart!!")
                                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        tpopularadd.setClickable(true);
                                    }
                                });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });

        }
    }

    private void addToCart(FoodModel foodItem) {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
       FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Cart")
                .document(currentUserUid)
                .collection("Items")
                .whereEqualTo("title", foodItem.getTitle())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        int currentQuantity = documentSnapshot.toObject(FoodModel.class).getNumberInCart();
                        int newQuantity = currentQuantity + 1;

                        documentSnapshot.getReference().update("numberInCart", newQuantity)
                                .addOnSuccessListener(aVoid -> {
                                    //showDialog("Item Added To Cart!");

                                    int position = -1;
                                    for (int i = 0; i < cartItems.size(); i++) {
                                        if (cartItems.get(i).getTitle().equals(foodItem.getTitle())) {
                                            position = i;
                                            break;
                                        }
                                    }

                                    if (position != -1) {
                                        cartItems.get(position).setNumberInCart(newQuantity);
                                        noteAdapter.notifyItemChanged(position); // Notify the adapter of the data change
                                    }

                                    updateCartItemCount();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(), "Failed to add item to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {

                        foodItem.setNumberInCart(1);
                        firebaseFirestore.collection("Cart")
                                .document(currentUserUid)
                                .collection("Items")
                                .document()
                                .set(foodItem)
                                .addOnSuccessListener(aVoid -> {
                                    //showDialog("Item Added To Cart!");

                                    cartItems.add(foodItem);
                                    updateCartItemCount();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(), "Failed to add item to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to add item to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void recyclerViewPopular(){

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Query query = firebaseFirestore.collection("Populars").document("1Pw88mB8PeEUjb1ibuMZ").collection("subcollection")
                .orderBy("title", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<FoodModel> allpopularitems = new FirestoreRecyclerOptions.Builder<FoodModel>()
                .setQuery(query, FoodModel.class)
                .setLifecycleOwner(this)
                .build();

        noteAdapter = new FirestoreRecyclerAdapter<FoodModel, HomeFragment.FoodViewHolder>(allpopularitems) {
            @Override
            protected void onBindViewHolder(@NonNull HomeFragment.FoodViewHolder holder, int position, @NonNull FoodModel model) {
                holder.tpopulartitle.setText(model.getTitle());
                holder.tpopularprice.setText(model.getPrice());

                String imageUrl=model.getImageUri();
                Log.d("ImageURL", "URL: " + model.getImageUri());
                if(imageUrl!=null){
                    Glide.with(HomeFragment.this)
                            .load(Uri.parse(imageUrl))
                            .into(holder.ipopularimg);
                }


                String docId = noteAdapter.getSnapshots().getSnapshot(position).getId();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), FoodDetails.class);
                        intent.putExtra("title", model.getTitle());
                        intent.putExtra("content", model.getDescription());
                        intent.putExtra("imageUri", model.getImageUri());
                        intent.putExtra("price", model.getPrice());
                        intent.putExtra("noteId", docId);
                        view.getContext().startActivity(intent);

                    }
                });


            }

            @NonNull
            @Override
            public HomeFragment.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View views = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_popular, parent, false);
                return new HomeFragment.FoodViewHolder(views);
            }


        };

        rvpopular.setAdapter(noteAdapter);


    }


    @Override
    public void onStart() {
        super.onStart();

            noteAdapter.startListening();

    }



    @Override
    public void onStop() {
        super.onStop();
        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }

    private void  replaceFragment(Fragment fragment,String tag){
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flfragment,fragment,tag)
                .commit();
    }

}