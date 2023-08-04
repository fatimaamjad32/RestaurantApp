package com.example.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import Models.FoodModel;

public class BurgerActivity2 extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private RecyclerView rvburger;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;


    FirestoreRecyclerAdapter<FoodModel, BurgerActivity2.FoodViewHolder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_burger2);

        Intent data = getIntent();

        rvburger = findViewById(R.id.rvburger2);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Query query = firebaseFirestore.collection("Burgers").document("iDIKIApKGbUx1XaUY3fS").collection("subcollection")
                .orderBy("title", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<FoodModel> allpopularitems = new FirestoreRecyclerOptions.Builder<FoodModel>()
                .setQuery(query, FoodModel.class).build();

        noteAdapter = new FirestoreRecyclerAdapter<FoodModel, BurgerActivity2.FoodViewHolder>(allpopularitems) {
            @Override
            protected void onBindViewHolder(@NonNull BurgerActivity2.FoodViewHolder holder, int position, @NonNull FoodModel model) {

                holder.tpopulartitle.setText(model.getTitle());
               holder.tpopularprice.setText(model.getPrice());


                String imageUrl=model.getImageUri();
                Log.d("ImageURL", "URL: " + model.getImageUri());
                if(imageUrl!=null){
                    Glide.with(BurgerActivity2.this)
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
                        intent.putExtra("noteId",docId);
                        view.getContext().startActivity(intent);

                    }
                });
            }

            @NonNull
            @Override
            public BurgerActivity2.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View views = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_popular, parent, false);
                return new BurgerActivity2.FoodViewHolder(views);
            }


        };


        rvburger.setHasFixedSize(true);


        rvburger.setLayoutManager(new GridLayoutManager(this, 2));
        rvburger.setItemAnimator(null);
        rvburger.setAdapter(noteAdapter);
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

    @Override
    protected void onStart() {
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

                                    showDialog("Item Added to Cart!");
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(BurgerActivity2.this, "Failed to add item to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {

                        foodItem.setNumberInCart(1);

                        firebaseFirestore.collection("Cart")
                                .document(currentUserUid)
                                .collection("Items")
                                .document()
                                .set(foodItem)
                                .addOnSuccessListener(aVoid -> {

                                showDialog("Item Added to Cart!");
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(BurgerActivity2.this, "Failed to add item to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BurgerActivity2.this, "Failed to add item to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BurgerActivity2.this);
        builder.setMessage(message)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}