package com.cleaningservice.cleaningservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import static android.app.PendingIntent.getActivity;

public class HomeActivity extends AppCompatActivity {

     BottomNavigationView navigation;
     FrameLayout frameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = new Intent(HomeActivity.this,FindCleanerActivity.class);
        startActivity(intent);

        navigation = findViewById(R.id.bottomNav);
        frameLayout = findViewById(R.id.frameLayout);
        Menu menu = navigation.getMenu();
        MenuItem item = menu.getItem(0);
        item.setChecked(true);


        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.navigation_profile:
                        Intent intent = new Intent(HomeActivity.this,ProfileActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_favlist:
                        Intent intent2 = new Intent(HomeActivity.this,FavoritesListActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.navigation_findcleaner:
                        Intent intent3 = new Intent(HomeActivity.this,FindCleanerActivity.class);
                        startActivity(intent3);
                       break;

                }
                return false;
            }
        });


    }


}
