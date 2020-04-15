package com.cleaningservice.cleaningservice.Worker;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import com.cleaningservice.cleaningservice.ApplicationDbContext;
import com.cleaningservice.cleaningservice.ProfileActivity;
import com.cleaningservice.cleaningservice.R;
import com.cleaningservice.cleaningservice.Util;
import com.google.android.material.navigation.NavigationView;
import java.sql.SQLException;
import java.util.List;

import Authentications.Preferences;
import Models.Favorite;
import Models.JobForm;

public class Favorites extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ApplicationDbContext _context = null;
    private DrawerLayout drawer;
    private List<Favorite> favoritesList;
    private Handler mainhandler = new Handler();
    private FavoritesAdapter favoritesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        try {
            _context = ApplicationDbContext.getInstance(getApplicationContext());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Toolbar toolbar2 = findViewById(R.id.sidebar);
        setSupportActionBar(toolbar2);

        drawer=findViewById(R.id.drawer_layout);
        NavigationView nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar2,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        RecyclerView recyclerView = findViewById(R.id.employee_favorite_list);
        findViewById(R.id.jobFormsProgressBar).setVisibility(View.VISIBLE);
        findViewById(R.id.employee_favorite_list).setVisibility(View.INVISIBLE);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                favoritesList = _context.GetUserFavoriteList(Preferences.GetLoggedInUserID(Favorites.this), Util.UserTypes.CUSTOMER);
                mainhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        favoritesAdapter = new FavoritesAdapter(favoritesList, getApplicationContext());
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.setAdapter(favoritesAdapter);
                        findViewById(R.id.jobFormsProgressBar).setVisibility(View.INVISIBLE);
                        findViewById(R.id.employee_favorite_list).setVisibility(View.VISIBLE);

                    }
                });
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Intent intent;
        switch(menuItem.getItemId()) {
            case R.id.navigation_employee_home:
                intent = new Intent(Favorites.this, Home.class);
                startActivity(intent);
                break;
            case R.id.navigation_profile:
                intent = new Intent(Favorites.this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.navigation_myjobs:
                intent = new Intent(Favorites.this,JobFormRequests.class);
                startActivity(intent);
                break;
            case R.id.navigation_favlist:
                intent = new Intent(Favorites.this,Favorites.class);
                startActivity(intent);
                break;
        }

        return false;
    }

}
