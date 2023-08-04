package com.example.restaurant;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Iterator;

//import Adapter.CartAdapter;
import Adapter.CartAdapter;
import Helper.ManagementCart;
import Models.FoodModel;
import Models.OrderModel;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartAdapterListener  {

    TextView ttotal,tsubtotal,tplaceorder;



    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    //private FoodModel object;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    int currentQuantity;
    private ArrayList<FoodModel> cartItems;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ttotal=findViewById(R.id.ttotal);
        tsubtotal=findViewById(R.id.tsubtotal);
        tplaceorder=findViewById(R.id.tplaceorder);


        Intent data = getIntent();

        recyclerView = findViewById(R.id.rvcart);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        tplaceorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cartItems.isEmpty())
                {
                    Toast.makeText(CartActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();
                }
                else {

                    Intent i = new Intent(CartActivity.this, UserActivity.class);
                    startActivity(i);

                }
            }
        });

        recyclerView = findViewById(R.id.rvcart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartItems = new ArrayList<>();

        cartAdapter = new CartAdapter(cartItems,  this);

        recyclerView.setAdapter(cartAdapter);

        getCartItemsFromFirestore();
    }

    private void getCartItemsFromFirestore() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        if (firebaseUser != null) {
            String currentUserUid = firebaseUser.getUid();
            CollectionReference cartCollection = firebaseFirestore.collection("Cart")
                    .document(currentUserUid)
                    .collection("Items");

            cartCollection.get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        cartItems.clear();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            FoodModel foodItem = documentSnapshot.toObject(FoodModel.class);
                            foodItem.setDocumentid(documentSnapshot.getId());
                            cartItems.add(foodItem);
                        }
                        cartAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(CartActivity.this, "Failed to fetch cart items: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    public void onSubtotalAndTotalUpdated(int subtotal, int total) {


        tsubtotal.setText(String.valueOf(subtotal));
        ttotal.setText(String.valueOf(total));
    }


    public interface CartDataCallback {
        void onCartDataLoaded(ArrayList<FoodModel> cartItems);
    }



}

