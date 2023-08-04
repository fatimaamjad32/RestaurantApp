package com.example.restaurant;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import Models.FoodModel;
import Models.OrderModel;

public class OrderActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    RecyclerView rvorder;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter<OrderModel, OrderActivity.OrderViewHolder> orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        rvorder = findViewById(R.id.rvorder);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();



        Query query = firebaseFirestore.collection("Orders")
                .orderBy("userId", Query.Direction.ASCENDING);

    FirestoreRecyclerOptions<OrderModel> allburgeritems = new FirestoreRecyclerOptions.Builder<OrderModel>()
            .setQuery(query, OrderModel.class).build();

    orderAdapter = new FirestoreRecyclerAdapter<OrderModel, OrderActivity.OrderViewHolder>(allburgeritems) {
        @Override
        protected void onBindViewHolder(@NonNull OrderActivity.OrderViewHolder holder, int position, @NonNull OrderModel model) {


            ImageView popupbutton=holder.itemView.findViewById(R.id.menupopbutton);

            FirebaseFirestore.getInstance().collection("Users").document(model.getUserId()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {

                            String userName = documentSnapshot.getString("name");
                            String userPhone = documentSnapshot.getString("phone");
                            String userAddress = documentSnapshot.getString("address");

                            StringBuilder orderDetailsBuilder = new StringBuilder();

                            orderDetailsBuilder.append("User Name: ").append(userName).append("\n\n");
                            orderDetailsBuilder.append("User Phone: ").append(userPhone).append("\n\n");
                            orderDetailsBuilder.append("User Address: ").append(userAddress).append("\n\n");


                            if (model.getOrderItems() != null) {
                                for (FoodModel orderItem : model.getOrderItems()) {
                                    orderDetailsBuilder.append("Item: ").append(orderItem.getTitle()).append(", \n")
                                            .append("Price: ").append(orderItem.getPrice()).append(",\n ")
                                            .append("Quantity: ").append(orderItem.getNumberInCart()).append("\n\n");
                                }
                            }

                            orderDetailsBuilder.append("Total Price: ").append(model.getTotalPrice());

                            holder.torderdetails.setText(orderDetailsBuilder.toString());
                        } else {

                        }
                    })
                    .addOnFailureListener(e -> {

                        Log.e("OrderActivity", "Error fetching user details: " + e.getMessage());
                    });



            String docId = orderAdapter.getSnapshots().getSnapshot(position).getId();



            popupbutton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu=new PopupMenu(view.getContext(),view);
                    popupMenu.setGravity(Gravity.END);

                    popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(@NonNull MenuItem menuItem) {


                            DocumentReference documentReference = firebaseFirestore.collection("Orders").document(docId);
                            documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(OrderActivity.this, "Order deleted successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(OrderActivity.this, "Order deletion failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });



        }


        @NonNull
        @Override
        public OrderActivity.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View views = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderlayout, parent, false);
            return new OrderActivity.OrderViewHolder(views);
        }


    };


        rvorder.setHasFixedSize(true);

        rvorder.setLayoutManager(new LinearLayoutManager(this));
       rvorder.setAdapter(orderAdapter);
}


    public class OrderViewHolder extends RecyclerView.ViewHolder{

        private TextView torderdetails;

        LinearLayout home2layout;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            torderdetails=itemView.findViewById(R.id.torderdetails);

        }


    }


    @Override
    protected void onStart(){
        super.onStart();
        orderAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (orderAdapter != null) {
            orderAdapter.stopListening();
        }
    }


}
