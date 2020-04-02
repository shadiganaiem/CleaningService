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
import android.view.MenuItem;

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
    private ApplicationDbContext _context=null;
    private ArrayList<Request> requests = new ArrayList<>();
    private static final String TAG = "Notifictions";
    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;

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

        drawer=findViewById(R.id.drawer_layout);
        NavigationView nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        recyclerView= findViewById(R.id.Requests_list);
        adapter = new RecyclerViewAdapter(this,requests);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.hasFixedSize();
        recyclerView.setAdapter(adapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initRecycleView();
                adapter.showShimmer =false;
                recyclerView.setAdapter(adapter);

            }
        },0);


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

                List<JobForm> jobForms = _context.GetJobFormsByCustomerID(_context.GetUser(GetLoggedInUserID(getApplicationContext())));
                for (JobForm form : jobForms) {
                    try {
                        ArrayList<Request> reqs = _context.GetFormUserRequests(form.ID);
                        for (Request request : reqs) {
                            requests.add(request);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
    }
}
