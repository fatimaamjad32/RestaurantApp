package com.example.restaurant;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddPastaActivity extends AppCompatActivity {

    EditText pastatitle,pastadescription,pastafee;
    Button btnpastapic,btnpastasave;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ImageView insertimg;
    FirebaseFirestore firebaseFirestore;
    private static final int IMAGE_PICKER_REQUEST_CODE = 1;
    private Map<String, Object> pastaitem;
    StorageReference storageReference;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pasta);


        Toolbar toolbar = findViewById(R.id.tbarcnote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pastatitle=findViewById(R.id.pastatitle);
        pastadescription=findViewById(R.id.pastadescription);
        pastafee=findViewById(R.id.pastafee);
        btnpastapic=findViewById(R.id.btnpastapic);
        btnpastasave=findViewById(R.id.btnpastasave);
        insertimg=findViewById(R.id.insertimg);

        storageReference = FirebaseStorage.getInstance().getReference();


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        pastaitem = new HashMap<>();

        btnpastapic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dexter.withContext(AddPastaActivity.this)
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

        btnpastasave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = pastatitle.getText().toString();
                String content = pastadescription.getText().toString();
                String price = pastafee.getText().toString();
                String search=title.toLowerCase();
                if (title.isEmpty() || content.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Both fields are required", Toast.LENGTH_SHORT).show();
                } else {


                    DocumentReference documentReference = firebaseFirestore.collection("Pastas")
                            .document("RBZutOiSl2pPNYn3UbYz").collection("subcollection").document();

                    pastaitem.put("title", title);
                    pastaitem.put("description", content);
                    pastaitem.put("price", price);
                    pastaitem.put("search", search);

                    documentReference.set(pastaitem).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Item Added Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("firebaseError", "onFailure: " +e);
                            Toast.makeText(getApplicationContext(), "Failed to Add Pasta "+e, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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


                        pastaitem.put("imageUri", uri.toString());
                        Toast.makeText(AddPastaActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e -> {
                        if (!isFinishing() && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(AddPastaActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}