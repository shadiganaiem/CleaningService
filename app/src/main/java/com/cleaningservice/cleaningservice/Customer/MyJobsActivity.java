package com.cleaningservice.cleaningservice.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;

import com.cleaningservice.cleaningservice.ApplicationDbContext;
import com.cleaningservice.cleaningservice.ProfileActivity;
import com.cleaningservice.cleaningservice.R;
import com.cleaningservice.cleaningservice.Worker.FormAdapter;
import com.google.android.material.navigation.NavigationView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import Models.JobForm;
import Models.Request;

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
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
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
                                    namesImages.add(new NameImage("",0));
                                }


                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
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
        }
        return false;
    }
}
