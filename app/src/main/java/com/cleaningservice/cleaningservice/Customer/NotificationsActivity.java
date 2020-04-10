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
import com.google.android.material.navigation.NavigationView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Models.JobForm;
import Models.Request;

import static Authentications.Preferences.GetLoggedInUserID;

public class NotificationsActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;
    public ApplicationDbContext _context=null;
    private ArrayList<Request> requests = new ArrayList<>();
    private static final String TAG = "Notifications";
    private  RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        try {
            _context = ApplicationDbContext.getInstance(getApplicationContext());
        } catch (
                SQLException e) {
            e.printStackTrace();
        }

        Toolbar toolbar = findViewById(R.id.sidebar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        recyclerView = findViewById(R.id.Requests_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(NotificationsActivity.this));
        adapter = new RecyclerViewAdapter(NotificationsActivity.this, requests);
        recyclerView.setAdapter(adapter);



        new Thread(){
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        initRecycleView();
                        adapter = new RecyclerViewAdapter(NotificationsActivity.this, requests);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(View.VISIBLE);
                        findViewById(R.id.ReqProgressBar).setVisibility(View.INVISIBLE);
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
                Intent intent = new Intent(NotificationsActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.navigation_favlist:
                Intent intent2 = new Intent(NotificationsActivity.this, FavoritesListActivity.class);
                startActivity(intent2);
                break;
            case R.id.navigation_findcleaner:
                Intent intent3 = new Intent(NotificationsActivity.this, FindCleanerActivity.class);
                startActivity(intent3);
                break;
            case R.id.navigation_myjobs:
                Intent intent4 = new Intent(NotificationsActivity.this, MyJobsActivity.class);
                startActivity(intent4);
                break;
            case R.id.navigation_notifications:
                break;
        }
        return false;
    }

    private void initRecycleView(){

                List<Integer> formIds = _context.GetJobFormID(_context.GetUser(GetLoggedInUserID(getApplicationContext())));
                for( Integer id : formIds) {
                    try {
                        ArrayList<Request> reqs = _context.GetFormUserRequests(id);
                        for (Request request : reqs) {
                            if(request.Status_id==5) {
                                requests.add(request);
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
    }


}
