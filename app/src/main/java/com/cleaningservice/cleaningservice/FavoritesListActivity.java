package com.cleaningservice.cleaningservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class FavoritesListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView navigation;
    private DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_list);


      /*  navigation = findViewById(R.id.bottomNav);
        Menu menu = navigation.getMenu();
        MenuItem item = menu.getItem(1);
        item.setChecked(true);*/


        Toolbar toolbar2 = findViewById(R.id.sidebar);
        setSupportActionBar(toolbar2);

       drawer=findViewById(R.id.drawer_layout);
        NavigationView nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar2,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.navigation_profile:
                Intent intent = new Intent(FavoritesListActivity.this,ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.navigation_favlist:
                break;
            case R.id.navigation_findcleaner:
                Intent intent2 = new Intent(FavoritesListActivity.this,FindCleanerActivity.class);
                startActivity(intent2);
                break;
            case R.id.navigation_myjobs:
                Intent intent4 = new Intent(FavoritesListActivity.this, MyJobsActivity.class);
                startActivity(intent4);
                break;
            case R.id.navigation_notifications:
                Intent intent5 = new Intent(FavoritesListActivity.this, NotificationsActivity.class);
                startActivity(intent5);
                break;

        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

}
