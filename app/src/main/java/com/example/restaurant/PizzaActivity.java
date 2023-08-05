package com.example.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import Models.FoodModel;

public class PizzaActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private RecyclerView rvpizza;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    FloatingActionButton addpitemfab;
    FirestoreRecyclerAdapter<FoodModel, PizzaActivity.FoodViewHolder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizza);

        Intent data = getIntent();

        rvpizza = findViewById(R.id.rvpizza);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();



        Query query = firebaseFirestore.collection("Pizzas").document("GvaS2kGxguxGCRDFFs3M")
                .collection("subcollection")
                .orderBy("title", Query.Direction.ASCENDING);


        FirestoreRecyclerOptions<FoodModel> allpopularitems = new FirestoreRecyclerOptions.Builder<FoodModel>()
                .setQuery(query, FoodModel.class).build();

        noteAdapter = new FirestoreRecyclerAdapter<FoodModel, PizzaActivity.FoodViewHolder>(allpopularitems) {
            @Override
            protected void onBindViewHolder(@NonNull PizzaActivity.FoodViewHolder holder, int position, @NonNull FoodModel model) {

                holder.tpopulartitle.setText(model.getTitle());
                holder.tpopularprice.setText(String.valueOf(model.getPrice()));
                Log.d("ImageURL", "URL: " + model.getImageUri());
                Glide.with(PizzaActivity.this)
                        .load(model.getImageUri())
                        .into(holder.ipopularimg);

                String docId = noteAdapter.getSnapshots().getSnapshot(position).getId();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), FoodDetails.class);
                        intent.putExtra("title", model.getTitle());
                        intent.putExtra("content", model.getDescription());
                        intent.putExtra("imageUri", model.getImageUri());
                        intent.putExtra("price",model.getPrice());
                        //  Toast.makeText(HomeActivity.this, "imageUri: "+ model.imageUri, Toast.LENGTH_SHORT).show();
                        intent.putExtra("noteId", docId);
                        view.getContext().startActivity(intent);

                    }
                });
            }

            @NonNull
            @Override
            public PizzaActivity.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View views = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_popular, parent, false);
                return new PizzaActivity.FoodViewHolder(views);
            }


        };


        rvpizza.setHasFixedSize(true);

        rvpizza.setLayoutManager(new GridLayoutManager(this,2));
        rvpizza.setItemAnimator(null);
        rvpizza.setAdapter(noteAdapter);
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder{

        private TextView tpopulartitle,tpopularprice,tpopularadd;
        ImageView ipopularimg;
        LinearLayout pizzalayout;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            tpopulartitle=itemView.findViewById(R.id.tpopulartitle);
            tpopularprice=itemView.findViewById(R.id.tpopularprice);
            tpopularadd=itemView.findViewById(R.id.tpopularadd);
            ipopularimg=itemView.findViewById(R.id.ipopularimg);
            pizzalayout=itemView.findViewById(R.id.pizzalayout);

            tpopularadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tpopularadd.setClickable(false);
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        FoodModel foodItem = noteAdapter.getItem(position);
                        addToCart(foodItem);
                        AlertDialog.Builder builder = new AlertDialog.Builder(PizzaActivity.this);
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

    @Override
    protected void onStart(){
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null) {
            noteAdapter.stopListening();
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
                                   // Toast.makeText(PizzaActivity.this, "Item added to cart!", Toast.LENGTH_SHORT).show();
                                    //showDialog("Item Added to Cart!");
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(PizzaActivity.this, "Failed to add item to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {

                        foodItem.setNumberInCart(1);

                        firebaseFirestore.collection("Cart")
                                .document(currentUserUid)
                                .collection("Items")
                                .document()
                                .set(foodItem)
                                .addOnSuccessListener(aVoid -> {
                                    //Toast.makeText(PizzaActivity.this, "Item added to cart!", Toast.LENGTH_SHORT).show();
                                    //showDialog("Item Added to Cart!");
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(PizzaActivity.this, "Failed to add item to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PizzaActivity.this, "Failed to add item to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

//    private void showDialog(String message) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(PizzaActivity.this);
//        builder.setMessage(message)
//                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//
//                    }
//                });
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }


}