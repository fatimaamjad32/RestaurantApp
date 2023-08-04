package com.example.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;

import Adapter.SearchAdapter;
import Models.FoodModel;

public class SearchActivity extends AppCompatActivity {
    EditText editsearch;
    TextView findtext;
    RecyclerView rvsearch;


    SearchAdapter searchAdapter;
    ArrayList<FoodModel> allResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        editsearch=findViewById(R.id.editsearch);
        findtext=findViewById(R.id.findtext);
        rvsearch=findViewById(R.id.rvsearch);


        allResults=new ArrayList<>();
        searchAdapter = new SearchAdapter(allResults, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                FoodModel currentItem = allResults.get(i);

                String docId = currentItem.getDocumentid();
                String title=currentItem.getTitle();
                String description=currentItem.getDescription();
                String price=currentItem.getPrice();
                String image=currentItem.getImageUri();
                Log.d("ImageURL", "URL: " + currentItem.getImageUri());




                Intent intent = new Intent(SearchActivity.this, FoodDetails.class);
                intent.putExtra("noteId", docId);
                intent.putExtra("title", title);
                intent.putExtra("content", description);
                intent.putExtra("price", price);
                intent.putExtra("imageUri", image);

                startActivity(intent);

            }
        });



        editsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                performSearch(editsearch.getText().toString());


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        rvsearch.setLayoutManager(new LinearLayoutManager(this));
        rvsearch.setItemAnimator(null);
        rvsearch.setAdapter(searchAdapter);

    }

    private void performSearch(String searchQuery) {
        if (!searchQuery.isEmpty()) {

            FirebaseFirestore.getInstance()
                    .collectionGroup("subcollection")
                    .orderBy("title")
                    .startAt(searchQuery)
                    .endAt(searchQuery + "\uf8ff")
                    .get(Source.SERVER)
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            findtext.setVisibility(View.GONE);

                            allResults.clear();


                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                Log.d("Testing1", "Search query: test");
                                FoodModel foodModel = document.toObject(FoodModel.class);
                                allResults.add(foodModel);
                            }
                            try {
                                searchAdapter.setData(allResults);
                            } catch (NullPointerException e) {
                                Toast.makeText(SearchActivity.this, "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            Log.d("Search", "Search query: " + searchQuery);
                            Log.d("Search", "Number of results: " + allResults.size());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Searcherror", "onFailure: " + e);
                            Toast.makeText(SearchActivity.this, "Search failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {

                findtext.setVisibility(View.VISIBLE);

                searchAdapter.setData(new ArrayList<>());
                allResults.clear();

        }

    }



}