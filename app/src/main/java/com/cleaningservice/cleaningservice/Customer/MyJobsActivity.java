package com.cleaningservice.cleaningservice.Customer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cleaningservice.cleaningservice.ApplicationDbContext;
import com.cleaningservice.cleaningservice.MainActivity;
import com.cleaningservice.cleaningservice.R;
import com.google.android.material.navigation.NavigationView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import Authentications.Preferences;
import Models.JobForm;

import static Authentications.Preferences.GetLoggedInUserID;

public class MyJobsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private static final String TAG = "Notifications";
    private RecyclerView recyclerView;
    private JobRecycler adapter;
    private ArrayList<JobForm> forms = new ArrayList<>();
    private ArrayList<NameImage> namesImages = new ArrayList<>();
    public ApplicationDbContext _context=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_my_jobs);


        try {
            _context = ApplicationDbContext.getInstance(getApplicationContext());
        } catch (
                SQLException e) {
            e.printStackTrace();
        }

        Toolbar toolbar = findViewById(R.id.sidebar);
        setSupportActionBar(toolbar);

        drawer=findViewById(R.id.drawer_layout);
        NavigationView nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        recyclerView = findViewById(R.id.formsRecycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new JobRecycler(this, forms , namesImages);
        recyclerView.setAdapter(adapter);

        new Thread(){
            public void run() {
                        try {
                            forms = _context.GetJobByID(_context.GetCustomerIdByUserID(GetLoggedInUserID(getApplicationContext())));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        Collections.reverse(forms);
                        for(JobForm form :forms){
                            try {
                                NameImage nameImage = _context.GetNameImage(form.ID);
                                if(nameImage!=null)
                                    namesImages.add(nameImage);
                                else{
                                    namesImages.add(new NameImage("",0, null));
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
           new Handler(Looper.getMainLooper()).post(new Runnable() {
                   @Override
                   public void run() {
                        adapter = new JobRecycler(MyJobsActivity.this, forms,namesImages);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(View.VISIBLE);
                        findViewById(R.id.FormsProgressBar).setVisibility(View.INVISIBLE);
                    }
                });
            }
        }.start();




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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.navigation_profile:
                Intent intent = new Intent(MyJobsActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.navigation_favlist:
                Intent intent2 = new Intent(MyJobsActivity.this, FavoritesListActivity.class);
                startActivity(intent2);
                break;
            case R.id.navigation_findcleaner:
                Intent intent3 = new Intent(MyJobsActivity.this, FindCleanerActivity.class);
                startActivity(intent3);
                break;
            case R.id.navigation_myjobs:
                break;
            case R.id.navigation_notifications:
                Intent intent4 = new Intent(MyJobsActivity.this, NotificationsActivity.class);
                startActivity(intent4);
                break;
            case R.id.navigation_signout:
                Preferences.Logout(this);
                Intent intent6 = new Intent(MyJobsActivity.this, MainActivity.class);
                startActivity(intent6);
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
