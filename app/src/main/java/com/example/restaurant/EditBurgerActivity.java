package com.example.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditBurgerActivity extends AppCompatActivity {
    EditText burgertitle, burgerdescription, burgerfee;
    Button btnburgerpic, btnburgersave;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ImageView insertimg;
    FirebaseFirestore firebaseFirestore;
    private static final int IMAGE_PICKER_REQUEST_CODE = 1;
    private Map<String, Object> burgeritem;
    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_burger);
        Toolbar toolbar = findViewById(R.id.tbarcnote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        burgertitle = findViewById(R.id.burgertitle);
        burgerdescription = findViewById(R.id.burgerdescription);
        burgerfee = findViewById(R.id.burgerfee);
        btnburgerpic = findViewById(R.id.btnburgerpic);
        btnburgersave = findViewById(R.id.btnburgersave);
        insertimg = findViewById(R.id.insertimg);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        burgeritem = new HashMap<>();


        Intent intent = getIntent();
        String burgerId = intent.getStringExtra("burgerId");
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String price = intent.getStringExtra("price");
        String imageUri = intent.getStringExtra("imageUri");


        burgertitle.setText(title);
        burgerdescription.setText(description);
        burgerfee.setText(price);


        Glide.with(this).load(imageUri).into(insertimg);


        btnburgersave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String updatedTitle = burgertitle.getText().toString();
                String updatedDescription = burgerdescription.getText().toString();
                String updatedPrice = burgerfee.getText().toString();
                String updatedSearch = updatedTitle.toLowerCase();


                DocumentReference documentReference = firebaseFirestore.collection("Burgers")
                        .document("iDIKIApKGbUx1XaUY3fS").collection("subcollection").document(burgerId);

                burgeritem.put("title", updatedTitle);
                burgeritem.put("description", updatedDescription);
                burgeritem.put("price", updatedPrice);
                burgeritem.put("search", updatedSearch);

                documentReference.update(burgeritem).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Item Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to update Burger", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        btnburgerpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(EditBurgerActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(galleryIntent, IMAGE_PICKER_REQUEST_CODE);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                            }
                        }).check();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                selectedImageUri = data.getData();
                insertimg.setImageURI(selectedImageUri);

                uploadImageAndGetDownloadUrl();
            }
        }
    }

    private void uploadImageAndGetDownloadUrl() {
        if (selectedImageUri != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());

            ref.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        if (!isFinishing() && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }


                        burgeritem.put("imageUri", uri.toString());
                        Toast.makeText(EditBurgerActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e -> {
                        if (!isFinishing() && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(EditBurgerActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}