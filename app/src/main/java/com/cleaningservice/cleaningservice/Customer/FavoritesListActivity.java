package com.cleaningservice.cleaningservice.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cleaningservice.cleaningservice.ApplicationDbContext;
import com.cleaningservice.cleaningservice.MainActivity;
import com.cleaningservice.cleaningservice.R;
import com.cleaningservice.cleaningservice.Util;
import com.google.android.material.navigation.NavigationView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import Authentications.Preferences;
import Models.Favorite;

import static Authentications.Preferences.GetLoggedInUserID;

public class FavoritesListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private RecyclerView recyclerView;
    private FavoritesRecycler adapter;
    private ArrayList<Favorite> favorites = new ArrayList<>();
    private ApplicationDbContext _context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_list);

        Toolbar toolbar2 = findViewById(R.id.sidebar);
        setSupportActionBar(toolbar2);

        drawer=findViewById(R.id.drawer_layout);
        NavigationView nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar2,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        try {
            _context = ApplicationDbContext.getInstance(getApplicationContext());
        } catch (
                SQLException e) {
            e.printStackTrace();
        }

        recyclerView = findViewById(R.id.favoritesRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FavoritesRecycler(this, favorites);
        recyclerView.setAdapter(adapter);

        new Thread(){
            public void run() {
                favorites = _context.GetUserFavoriteList(GetLoggedInUserID(getApplicationContext()),Util.UserTypes.EMPLOYEE);
                Collections.reverse(favorites);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new FavoritesRecycler(FavoritesListActivity.this, favorites);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(View.VISIBLE);
                        findViewById(R.id.FavProgressBar).setVisibility(View.INVISIBLE);
                    }
                });
            }
        }.start();


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.navigation_profile:
                Intent intent = new Intent(FavoritesListActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.navigation_favlist:
                break;
            case R.id.navigation_findcleaner:
                Intent intent2 = new Intent(FavoritesListActivity.this, FindCleanerActivity.class);
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
            case R.id.navigation_signout:
                Preferences.Logout(this);
                Intent intent6 = new Intent(FavoritesListActivity.this, MainActivity.class);
                startActivity(intent6);
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
