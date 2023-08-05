package com.example.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class ForgotPasswordActivity extends AppCompatActivity {

    TextView gobacklogin,btnrecover;
    EditText ifemail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        gobacklogin=findViewById(R.id.gobacklogin);
        btnrecover=findViewById(R.id.btnrecover);
        ifemail=findViewById(R.id.ifemail);

        gobacklogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(ForgotPasswordActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        btnrecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ifemail.getText().toString().trim();
               isValidEmail(email);

            }
        });
    }

    private void isValidEmail(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            SignInMethodQueryResult result = task.getResult();
                            if (result != null && result.getSignInMethods() != null && !result.getSignInMethods().isEmpty()) {
                                sendRecoveryEmail(email);
                            } else {
                                ifemail.setError("Email not registered");
                            }
                        } else {
                             Toast.makeText(ForgotPasswordActivity.this, "Error checking email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void sendRecoveryEmail(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                             Toast.makeText(ForgotPasswordActivity.this, "Password recovery email sent", Toast.LENGTH_SHORT).show();
                        } else {
                             Toast.makeText(ForgotPasswordActivity.this, "Error sending recovery email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}