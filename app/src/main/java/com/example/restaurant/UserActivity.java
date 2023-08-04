package com.example.restaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import Models.FoodModel;
import Models.OrderModel;
import Models.UserModel;

public class UserActivity extends AppCompatActivity {

    EditText username,userphone,useraddress;
    TextView btnsaveuser;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseUser firebaseUser;
    private ArrayList<FoodModel> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        username=findViewById(R.id.username);
        userphone=findViewById(R.id.userphone);
        useraddress=findViewById(R.id.useraddress);
        btnsaveuser=findViewById(R.id.btnsaveuser);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        btnsaveuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = username.getText().toString().trim();
                String phone = userphone.getText().toString().trim();
                String address = useraddress.getText().toString().trim();

                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();



                    UserModel user = new UserModel(userId, name, phone, address);

    firestore.collection("Users").document(userId).set(user)
            .addOnSuccessListener(aVoid -> {
                if(!name.isEmpty()&& !phone.isEmpty() && !address.isEmpty()) {
                    checkUserDetailsInFirestore();
                    Intent i = new Intent(UserActivity.this, LastActivity.class);
                    startActivity(i);
                    clearCart();
                }else {
                    Toast.makeText(UserActivity.this, "All Fields are required", Toast.LENGTH_SHORT).show();
                }


            })
            .addOnFailureListener(e -> {
                Toast.makeText(UserActivity.this, "Failed to save details"+e, Toast.LENGTH_SHORT).show();
            });
}else {

}


                }

        });

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            firestore.collection("Users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {

                            UserModel user = documentSnapshot.toObject(UserModel.class);
                            if (user != null) {

                                username.setText(user.getName());
                                userphone.setText(user.getPhone());
                                useraddress.setText(user.getAddress());
                            }
                        } else {

                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(UserActivity.this, "Failed to fetch user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

    }


    private void getCartItems(CartActivity.CartDataCallback callback) {
        String currentUserUid = firebaseUser.getUid();
        CollectionReference cartCollection = firestore.collection("Cart")
                .document(currentUserUid)
                .collection("Items");

        cartItems = new ArrayList<>();

        cartCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        FoodModel foodItem = documentSnapshot.toObject(FoodModel.class);
                        foodItem.setDocumentid(documentSnapshot.getId());
                        cartItems.add(foodItem);
                    }


                    callback.onCartDataLoaded(cartItems);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UserActivity.this, "Failed to fetch cart items: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    callback.onCartDataLoaded(cartItems);
                });
    }

    private void checkUserDetailsInFirestore() {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Users").document(currentUserUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();


                        getCartItems(new CartActivity.CartDataCallback() {
                            @Override
                            public void onCartDataLoaded(ArrayList<FoodModel> cartItems) {

                                placeOrderInFirestore(cartItems);
                            }
                        });

                    }
                });
    }





    private void placeOrderInFirestore(ArrayList<FoodModel> cartItems) {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        firestore.collection("Users").document(currentUserUid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserModel user = documentSnapshot.toObject(UserModel.class);
                        if (user != null) {

                            int totalPrice = calculateTotalPrice(cartItems);



                            user.setTotalPrice(Integer.parseInt(String.valueOf(totalPrice)));


                            OrderModel orderModel = new OrderModel();
                            orderModel.setUserId(currentUserUid);
                            orderModel.setUsername(user.getName());
                            orderModel.setPhone(user.getPhone());
                            orderModel.setAddress(user.getAddress());
                            orderModel.setOrderItems(cartItems);
                            orderModel.setTotalPrice(Integer.parseInt(String.valueOf(totalPrice)));



                            firestore.collection("Orders").document()
                                    .set(orderModel)
                                    .addOnSuccessListener(aVoid -> {

                                        firestore.collection("Users").document(currentUserUid).set(user)
                                                .addOnSuccessListener(aVoid1 -> {
                                                    Toast.makeText(UserActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(UserActivity.this, "Failed to place the order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(UserActivity.this, "Failed to place the order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(UserActivity.this, "User details not found in Firestore.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UserActivity.this, "Failed to fetch user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private int calculateTotalPrice(List<FoodModel> cartItems) {

        int subtotal = 0;
        int total = 0;

        if (cartItems != null) {
            for (FoodModel foodModel : cartItems) {
                int itemPrice = Integer.parseInt(foodModel.getPrice());
                int itemQuantity = foodModel.getNumberInCart();
                int itemTotal = itemPrice * itemQuantity;
                subtotal += itemTotal;
            }
        }


        total = subtotal + 100;
        return total;
    }



    private void clearCart() {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


        firebaseFirestore.collection("Cart")
                .document(currentUserUid)
                .collection("Items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        documentSnapshot.getReference().delete();
                    }


                })
                .addOnFailureListener(e -> {

                });
    }
}