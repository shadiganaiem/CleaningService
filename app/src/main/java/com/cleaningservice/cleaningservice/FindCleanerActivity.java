package com.cleaningservice.cleaningservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FindCleanerActivity extends AppCompatActivity {

    BottomNavigationView navigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_cleaner);

        navigation = findViewById(R.id.bottomNav);
        Menu menu = navigation.getMenu();
        MenuItem item = menu.getItem(0);
        item.setChecked(true);



        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.navigation_profile:
                        Intent intent = new Intent(FindCleanerActivity.this,ProfileActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_favlist:
                        Intent intent2 = new Intent(FindCleanerActivity.this,FavoritesListActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.navigation_findcleaner:
                        break;

                }
                return false;
            }
        });
    }
}
