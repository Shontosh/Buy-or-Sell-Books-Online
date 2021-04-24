package com.lab69.booksellapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.lab69.booksellapp.Fragments.HomeFragment;
import com.lab69.booksellapp.Fragments.NotificationFragment;
import com.lab69.booksellapp.Fragments.ProfileFragment;
import com.lab69.booksellapp.Fragments.SearchFragment;

import static android.content.Context.MODE_PRIVATE;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation_id);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        Bundle intent = getIntent().getExtras();
        if (intent != null){
            String publisher = intent.getString("publisherid");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }

    }


    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedFragment= new HomeFragment();
                    break;
                case R.id.nav_search:
                    selectedFragment=new SearchFragment();
                    break;
                case R.id.nav_add:
                    selectedFragment=null;
                    startActivity(new Intent(MainActivity.this,PostActivity.class));
                    break;
                case R.id.nav_heart:
                    selectedFragment=new NotificationFragment();
                    break;
                case R.id.nav_person:
                    SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                    editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();
                    selectedFragment=new ProfileFragment();
                    break;


            }
            if (selectedFragment!=null)
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            }

            return true;
        }

    };
}