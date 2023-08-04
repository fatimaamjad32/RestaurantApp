package com.example.restaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;

import Helper.ManagementCart;
import Models.FoodModel;

public class FoodDetails extends AppCompatActivity {

    private TextView taddtocart, detailtitle, detaildescription, detailprice, plus, minus, number;

    ImageView detailimg;
    int number2 = 0;
    private FoodModel object;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);
        detailtitle = findViewById(R.id.detailtitle);
        detaildescription = findViewById(R.id.detaildescription);
        detailprice = findViewById(R.id.detailprice);
        detailimg = findViewById(R.id.detailimg);
        plus = findViewById(R.id.plus);
        minus = findViewById(R.id.minus);
        number = findViewById(R.id.number);
        taddtocart = findViewById(R.id.taddtocart);
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        ManagementCart managementCart = new ManagementCart(this, firebaseFirestore, currentUserUid);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(number2<50) {
                    number2++;
                    number.setText(String.valueOf(number2));
                }
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (number2 > 1) {
                    number2--;
                    number.setText(String.valueOf(number2));
                }
            }
        });


        Intent data = getIntent();



        String imageUrl =  data.getStringExtra("imageUri");
        Glide.with(this)
                .load(imageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        Log.e("Glide", "Image loading failed: " + e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(detailimg);


        detaildescription.setText(data.getStringExtra("content"));
        detailtitle.setText(data.getStringExtra("title"));
        detailprice.setText(data.getStringExtra("price"));

        number.setText("1");


        taddtocart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                int quantity = Integer.parseInt(number.getText().toString());
                if (quantity > 0) {


                    String title = detailtitle.getText().toString();
                    String description = detaildescription.getText().toString();
                    String price = detailprice.getText().toString();
                    FoodModel model = new FoodModel();
                    String imageUri = model.getImageUri();


                    object = new FoodModel(title, imageUri, description, price, quantity);


                    managementCart.getListCart(new ManagementCart.CartDataCallback() {
                        @Override
                        public void onCartDataLoaded(ArrayList<FoodModel> cartItems) {





                            managementCart.insertFood(object, cartItems);


                        }




                    });


                } else {
                    Toast.makeText(FoodDetails.this, "Quantity is zero ", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }


}