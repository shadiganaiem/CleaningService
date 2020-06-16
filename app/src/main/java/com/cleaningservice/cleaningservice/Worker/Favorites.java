package com.cleaningservice.cleaningservice.Worker;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cleaningservice.cleaningservice.ApplicationDbContext;
import com.cleaningservice.cleaningservice.MainActivity;
import com.cleaningservice.cleaningservice.R;
import com.cleaningservice.cleaningservice.Util;
import com.google.android.material.navigation.NavigationView;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import Authentications.Preferences;
import Models.Favorite;

public class Favorites extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ApplicationDbContext _context = null;
    private DrawerLayout drawer;
    private List<Favorite> favoritesList;
    private Handler mainhandler = new Handler();
    private FavoritesAdapter favoritesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
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
                intent = new Intent(Favorites.this, WorkerProfile.class);
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
            case R.id.navigation_signout:
                Preferences.Logout(this);
                Intent intent6 = new Intent(Favorites.this, MainActivity.class);
                startActivity(intent6);
                break;
            case R.id.navigation_jobOffers:
                Intent intent5 = new Intent(Favorites.this,JobProposals.class);
                startActivity(intent5);
                break;
            case R.id.change_language:
                showLanguageDialog();
                break;
        }
        return false;
    }

    private void showLanguageDialog() {
        final String[] languagesList = { "العربية", "עברית" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.chooseLanguage));
        builder.setSingleChoiceItems(languagesList, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0) {
                    setLocale("ar");
                    recreate();
                }
                else if (which ==1){
                    setLocale("he");
                    recreate();
                }
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void setLocale(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = prefs.getString("My_Lang","");
        setLocale(lang);
    }
}


