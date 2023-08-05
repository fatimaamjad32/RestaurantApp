package com.example.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView bottomNavigationView=findViewById(R.id.bnv);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.homeitem);
        FirebaseApp.initializeApp(this);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.homeitem)
        {
            HomeFragment homeFragment=(HomeFragment) getSupportFragmentManager().findFragmentByTag("home_fragment");
            if (homeFragment!=null){
                if (!homeFragment.isAdded() && !homeFragment.isVisible()){
                    replaceFragment(new HomeFragment(),"home_fragment");
                }
            }else {
                replaceFragment(new HomeFragment(),"home_fragment");
            }

            return true;
        }
        if(item.getItemId()==R.id.menuitem)
        {
            MenuFragment menuFragment=(MenuFragment) getSupportFragmentManager().findFragmentByTag("menu_fragment") ;
            if (menuFragment!=null){
                if (!menuFragment.isAdded() && !menuFragment.isVisible()){
                    replaceFragment(new MenuFragment(),"menu_fragment");
                }
            }else {
                replaceFragment(new MenuFragment(),"menu_fragment");
            }

            return true;
        }

        return false;
    }

    private void  replaceFragment(Fragment fragment,String tag){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flfragment,fragment,tag)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        replaceFragment(new HomeFragment(),"home_fragment");
    }
    @Override
    public void onBackPressed() {
        MenuFragment menuFragment=(MenuFragment) getSupportFragmentManager().findFragmentByTag("menu_fragment") ;
        if (menuFragment!=null){
            if (menuFragment.isAdded() && menuFragment.isVisible()){
                replaceFragment(new HomeFragment(),"home_fragment");
            }
        }
        HomeFragment homeFragment=(HomeFragment) getSupportFragmentManager().findFragmentByTag("home_fragment");
        if (homeFragment!=null){
            if (homeFragment.isAdded() && homeFragment.isVisible()){
                finish();
            }
        }
    }
}