package com.example.restaurant;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import Models.FoodModel;

public class DealsActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    FloatingActionButton adddealsfab;
    FirestoreRecyclerAdapter<FoodModel, DealsActivity.FoodViewHolder> noteAdapter;
    private static final int EDIT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals);

        Intent data = getIntent();

        recyclerView = findViewById(R.id.rvdeal);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        adddealsfab = findViewById(R.id.addburgerfab);
        adddealsfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DealsActivity.this, AddDealsActivity.class);
                startActivity(i);

            }
        });

        Query query = firebaseFirestore.collection("Deals").document("YVGmwDdGTfsYZ7djongn").collection("subcollection")
                .orderBy("title", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<FoodModel> allburgeritems = new FirestoreRecyclerOptions.Builder<FoodModel>()
                .setQuery(query, FoodModel.class).build();

        noteAdapter = new FirestoreRecyclerAdapter<FoodModel, DealsActivity.FoodViewHolder>(allburgeritems) {
            @Override
            protected void onBindViewHolder(@NonNull DealsActivity.FoodViewHolder holder, int position, @NonNull FoodModel model) {
                ImageView popupbutton=holder.itemView.findViewById(R.id.menupopbutton);
                holder.hometitle2.setText(model.getTitle());
                holder.homecontent2.setText(model.getDescription());
                Log.d("ImageURL", "URL: " + model.getImageUri());
                Glide.with(DealsActivity.this)
                        .load(model.getImageUri())
                        .into(holder.homeimg2);

                String docId = noteAdapter.getSnapshots().getSnapshot(position).getId();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), FoodDetails.class);
                        intent.putExtra("title", model.getTitle());
                        intent.putExtra("content", model.getDescription());
                        intent.putExtra("imageUri", model.getImageUri());
                        intent.putExtra("price",model.getPrice());

                        intent.putExtra("noteId", docId);
                        view.getContext().startActivity(intent);

                    }
                });

                popupbutton.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {
                        PopupMenu popupMenu=new PopupMenu(view.getContext(),view);
                        popupMenu.setGravity(Gravity.END);

                        popupMenu.getMenu().add("delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {


                                DocumentReference documentReference=firebaseFirestore.collection("Deals").
                                        document("YVGmwDdGTfsYZ7djongn").collection("subcollection").document(docId);
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(DealsActivity.this, "note deleted successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(DealsActivity.this, "note deletion filed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });
                        popupMenu.getMenu().add("edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {

                                editBurger(model, docId);
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }

            @NonNull
            @Override
            public DealsActivity.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View views = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular2, parent, false);
                return new DealsActivity.FoodViewHolder(views);
            }


        };


        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(noteAdapter);
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder{

        private TextView hometitle2,homecontent2;
        ImageView homeimg2;
        LinearLayout home2layout;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            hometitle2=itemView.findViewById(R.id.hometitle2);
            homecontent2=itemView.findViewById(R.id.homecontent2);
            homeimg2=itemView.findViewById(R.id.homeimg2);
            home2layout=itemView.findViewById(R.id.home2layout);

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

    private void editBurger(FoodModel foodModel, String docId) {
        Intent intent = new Intent(this, EditDealsActivity.class);
        intent.putExtra("burgerId", docId);
        intent.putExtra("title", foodModel.getTitle());
        intent.putExtra("description", foodModel.getDescription());
        intent.putExtra("price", foodModel.getPrice());
        intent.putExtra("imageUri", foodModel.getImageUri());
        startActivityForResult(intent, EDIT_CODE);
    }


}