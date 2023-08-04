package Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.restaurant.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import Models.FoodModel;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {



    private ArrayList<FoodModel> cartItems;
    private int subtotal;
    private int total;
    FirebaseFirestore firebaseFirestore;

    private CartAdapterListener adapterListener;


public CartAdapter(ArrayList<FoodModel> cartItems, CartAdapterListener adapterListener) {
    this.cartItems = cartItems;
    //this.onCartItemDeleteListener = listener;
    this.adapterListener = adapterListener;

}

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);

        calculateSubtotalAndTotal();



        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {
        firebaseFirestore=FirebaseFirestore.getInstance();
        FoodModel model = cartItems.get(position);
        holder.tvTitle.setText(model.getTitle());
        holder.tvPrice.setText(String.valueOf(model.getPrice()));
        holder.tvQuantity.setText(String.valueOf(model.getNumberInCart()));

        Glide.with(holder.itemView.getContext())
                .load(model.getImageUri())
                .into(holder.cartimg);


        holder.cartplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = model.getNumberInCart();
                if(currentQuantity<50){
                    currentQuantity++;
                    model.setNumberInCart(currentQuantity);
                    holder.tvQuantity.setText(String.valueOf(currentQuantity));
                    calculateSubtotalAndTotal();

                    updateCartItemQuantityInFirestore(model.getDocumentid(),currentQuantity);
                }

            }
        });

        holder.cartminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuantity = model.getNumberInCart();
                currentQuantity--;
                model.setNumberInCart(currentQuantity);
                holder.tvQuantity.setText(String.valueOf(currentQuantity));
                calculateSubtotalAndTotal();

                updateCartItemQuantityInFirestore(model.getDocumentid(), currentQuantity);

                if (currentQuantity == 0) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        cartItems.remove(adapterPosition);
                        notifyItemRemoved(adapterPosition);
                        deleteCartItemFromFirestore(model.getDocumentid());
                    }
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvPrice, tvQuantity,cartplus,cartminus;
        ImageView cartimg;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.carttitle);
            tvPrice = itemView.findViewById(R.id.cartprice);
            tvQuantity = itemView.findViewById(R.id.cartquantity);
            cartminus = itemView.findViewById(R.id.cartminus);
            cartplus = itemView.findViewById(R.id.cartplus);
            cartimg=itemView.findViewById(R.id.cartimg);

        }

    }

    private void updateCartItemQuantityInFirestore(String documentId, int quantity) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = firebaseFirestore.collection("Cart")
                .document(currentUserUid)
                .collection("Items")
                .document(documentId);

        documentReference.update("numberInCart", quantity)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void deleteCartItemFromFirestore(String documentId) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = firebaseFirestore.collection("Cart")
                .document(currentUserUid)
                .collection("Items")
                .document(documentId);

        documentReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void calculateSubtotalAndTotal() {
        subtotal = 0;
        total = 0;
        if (cartItems != null) {
            for (FoodModel foodModel : cartItems) {
                int itemPrice = Integer.parseInt(foodModel.getPrice());
                int itemQuantity = foodModel.getNumberInCart();
                int itemTotal = itemPrice * itemQuantity;
                subtotal += itemTotal;
            }
        }
        total = subtotal + 100;
        adapterListener.onSubtotalAndTotalUpdated(subtotal, total);
    }

//    private void updateSubtotalAndTotal(String priceChange) {
//        int priceChangeValue = Integer.parseInt(priceChange);
//        subtotal += priceChangeValue;
//        total = subtotal + 100;
//        adapterListener.onSubtotalAndTotalUpdated(subtotal, total);
//    }


    public interface CartAdapterListener {
        void onSubtotalAndTotalUpdated(int subtotal, int total);
    }

}
