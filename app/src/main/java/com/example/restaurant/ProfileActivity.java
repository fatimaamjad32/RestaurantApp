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
import com.google.firebase.firestore.FirebaseFirestore;

import Models.UserModel;

public class ProfileActivity extends AppCompatActivity {

    EditText username,userphone,useraddress;
    TextView btnsaveuser;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
                                if (!name.isEmpty() && !phone.isEmpty() && !address.isEmpty()) {

                                    Toast.makeText(ProfileActivity.this, "Details Updated", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(ProfileActivity.this, "All Fields are required", Toast.LENGTH_SHORT).show();
                                }


                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(ProfileActivity.this, "Failed to save details" + e, Toast.LENGTH_SHORT).show();
                            });
                } else {

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
                            Toast.makeText(ProfileActivity.this, "Failed to fetch user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }


        }


}
