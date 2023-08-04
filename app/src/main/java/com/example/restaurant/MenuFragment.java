package com.example.restaurant;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;

import Adapter.SearchAdapter;
import Models.FoodModel;


public class MenuFragment extends Fragment  {

    private FirebaseAuth firebaseAuth;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    ImageView popupbutton;


    RecyclerView  rvdeals;
    TextView editsearch;

    FirestoreRecyclerAdapter<FoodModel, MenuFragment.FoodViewHolder> noteAdapter;



    public MenuFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_menu, container, false);

        editsearch = rootview.findViewById(R.id.editsearch);
        rvdeals = rootview.findViewById(R.id.rvdeals);
        popupbutton = rootview.findViewById(R.id.popupbutton);

        editsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),SearchActivity.class));
            }
        });

        popupbutton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.setGravity(Gravity.END);

                popupMenu.getMenu().add("Logout").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem menuItem) {

                        firebaseAuth.signOut();

                        startActivity(new Intent(getContext(), LoginActivity.class));
                        if (getActivity() != null) {
                            getActivity().finish();
                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });




        recyclerViewDeals();


        return rootview;
    }



    private void recyclerViewDeals() {


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Query query = firebaseFirestore.collection("Deals").document("YVGmwDdGTfsYZ7djongn").collection("subcollection")
                .orderBy("title", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<FoodModel> allpopularitems = new FirestoreRecyclerOptions.Builder<FoodModel>()
                .setQuery(query, FoodModel.class).build();


        noteAdapter = new FirestoreRecyclerAdapter<FoodModel, MenuFragment.FoodViewHolder>(allpopularitems) {
            @Override
            protected void onBindViewHolder(@NonNull MenuFragment.FoodViewHolder holder, int position, @NonNull FoodModel model) {

                holder.tpopulartitle.setText(model.getTitle());
                holder.tpopularprice.setText(model.getPrice());


                String imageUrl = model.getImageUri();
                Log.d("ImageURL", "URL: " + model.getImageUri());
                if (imageUrl != null) {
                    Glide.with(MenuFragment.this)
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
            public MenuFragment.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View views = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_popular, parent, false);
                return new MenuFragment.FoodViewHolder(views);
            }


        };


        rvdeals.setHasFixedSize(true);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);

        rvdeals.setLayoutManager(gridLayoutManager);
        rvdeals.setAdapter(noteAdapter);
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
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        FoodModel foodItem = noteAdapter.getItem(position);
                        addToCart(foodItem);
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
                                    Toast.makeText(requireContext(), "Item added to cart!", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(requireContext(), "Item added to cart!", Toast.LENGTH_SHORT).show();
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

}