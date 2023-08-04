package com.example.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity2 extends AppCompatActivity {

    Button btnaddpizza,btnaddburger,btnaddpasta,btnadddonut,btnadddrink,btnorders,btnaddpopular,btndeals;

    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        getSupportActionBar().setTitle("Restaurant");

        btnaddpizza=findViewById(R.id.btnaddpizza);
        btnaddburger=findViewById(R.id.btnaddburger);
        btnaddpasta=findViewById(R.id.btnaddpasta);
        btnadddonut=findViewById(R.id.btnadddonut);
        btnadddrink=findViewById(R.id.btnadddrink);
        btnorders=findViewById(R.id.btnorders);
        btnaddpopular=findViewById(R.id.btnaddpopular);
        btndeals=findViewById(R.id.btndeals);

        firebaseAuth=FirebaseAuth.getInstance();

        btnaddpizza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity2.this,HomeActivity2.class);
                startActivity(i);
                finish();

            }
        });
        btnaddburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity2.this,BurgerActivity.class);
                startActivity(i);
                finish();

            }
        });
        btnaddpasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity2.this,PastaActivity.class);
                startActivity(i);
                finish();

            }
        });
        btnadddonut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity2.this,DonutActivity.class);
                startActivity(i);
                finish();

            }
        });
        btnadddrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity2.this,DrinkActivity.class);
                startActivity(i);
                finish();

            }
        });
        btnorders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity2.this,OrderActivity.class);
                startActivity(i);
                finish();

            }
        });
        btnaddpopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity2.this,Popular.class);
                startActivity(i);
                finish();

            }
        });
        btndeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity2.this,DealsActivity.class);
                startActivity(i);
                finish();

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if (item.getItemId() == R.id.btnlogout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(MainActivity2.this, LoginActivity.class));
        }


        return  super.onOptionsItemSelected(item);
    }


}