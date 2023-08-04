package Helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import Adapter.CartAdapter;
import Models.FoodModel;

public class ManagementCart {

    private Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private CollectionReference cartCollection;
    ArrayList<FoodModel> cartItems = new ArrayList<>();

    public ManagementCart(Context context, FirebaseFirestore firebaseFirestore, String currentUserUid) {
        this.context = context;
        this.firebaseFirestore = firebaseFirestore;
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.cartCollection = firebaseFirestore.collection("Cart").document(currentUserUid).collection("Items");
    }



    public void insertFood(FoodModel item, ArrayList<FoodModel> listFood) {
        boolean existAlready = false;
        int n = 0;
        for (int i = 0; i < listFood.size(); i++) {
            if (listFood.get(i).getTitle().equals(item.getTitle())) {
                existAlready = true;
                n = i;
                break;
            }
        }

        if (existAlready) {
            int newQuantity = listFood.get(n).getNumberInCart() + item.getNumberInCart();
            listFood.get(n).setNumberInCart(newQuantity);
            String documentId = listFood.get(n).getDocumentid();

            if (documentId != null) {
                cartCollection.document(documentId)
                        .update("numberInCart", newQuantity)
                        .addOnSuccessListener(aVoid -> {
                            // Toast.makeText(context, "Quantity updated", Toast.LENGTH_SHORT).show();
                            showDialog("Quantity Updated in Cart");
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to update quantity", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(context, "Document ID is null", Toast.LENGTH_SHORT).show();
            }
        }

        else {

            listFood.add(item);

            Map<String, Object> foodData = new HashMap<>();
            foodData.put("title", item.getTitle());
            foodData.put("imageUri", item.getImageUri());
            foodData.put("description", item.getDescription());
            foodData.put("price", item.getPrice());
            foodData.put("numberInCart", item.getNumberInCart());

            cartCollection.add(foodData)
                    .addOnSuccessListener(documentReference -> {
                        item.setDocumentid(documentReference.getId());
                        showDialog("Item added to cart");

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to add item to cart", Toast.LENGTH_SHORT).show();
                    });
        }


    }
    public interface CartDataCallback {
        void onCartDataLoaded(ArrayList<FoodModel> cartItems);
    }



    public void getListCart(CartDataCallback callback) {

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


                    callback.onCartDataLoaded(cartItems);
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(context, "Failed to fetch cart items: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    callback.onCartDataLoaded(cartItems);
                });
    }



    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
