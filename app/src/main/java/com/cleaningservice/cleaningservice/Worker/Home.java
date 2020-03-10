package com.cleaningservice.cleaningservice.Worker;
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
import android.widget.ProgressBar;

import com.cleaningservice.cleaningservice.ApplicationDbContext;
import com.cleaningservice.cleaningservice.Customer.FindCleanerActivity;
import com.cleaningservice.cleaningservice.ProfileActivity;
import com.cleaningservice.cleaningservice.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import Models.JobForm;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TabLayout.OnTabSelectedListener {

    private DrawerLayout drawer;
    private FormAdapter jobFormAdapter;
    private ApplicationDbContext _context = null;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_home);

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

        tabLayout = findViewById(R.id.TabLayout);
        tabLayout.setOnTabSelectedListener(this);

        RecyclerView view = findViewById(R.id.job_form_list);
        new Thread(){
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        List<JobForm> jobForms = _context.GetJobForms();
                        jobFormAdapter = new FormAdapter(jobForms,getApplicationContext());
                        view.setAdapter(jobFormAdapter);
                        view.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                        findViewById(R.id.jobFormsProgressBar).setVisibility(View.INVISIBLE);
                        findViewById(R.id.job_form_list).setVisibility(View.VISIBLE);
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

    /**
     * Move From Home Activity to another Activity Using NavigationBar
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Intent intent;
        switch(menuItem.getItemId()) {
            case R.id.navigation_employee_home:
                intent = new Intent(Home.this, Home.class);
                startActivity(intent);
                break;
            case R.id.navigation_make_worker_card:
                intent = new Intent(Home.this, SetWorkingDetailsActivity.class);
                startActivity(intent);
                break;
            case R.id.navigation_profile:
                intent = new Intent(Home.this, ProfileActivity.class);
                startActivity(intent);
                break;
        }

        return false;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        RecyclerView view = findViewById(R.id.job_form_list);
        findViewById(R.id.jobFormsProgressBar).setVisibility(View.VISIBLE);
        findViewById(R.id.job_form_list).setVisibility(View.INVISIBLE);
        switch (tab.getPosition()){
            case 0:
                new Thread(){
                    @Override
                    public void run() {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                List<JobForm> jobForms = _context.GetJobForms();
                                jobFormAdapter = new FormAdapter(jobForms,getApplicationContext());
                                view.setAdapter(jobFormAdapter);
                                view.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                                //jobFormAdapter.UpdateList(jobForms);
                                findViewById(R.id.jobFormsProgressBar).setVisibility(View.INVISIBLE);
                                findViewById(R.id.job_form_list).setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }.start();

                break;
            case 1:
                new Thread(){
                    @Override
                    public void run() {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                List<JobForm> jobForms = _context.GetJobFormsForThisWeek();
                                jobFormAdapter = new FormAdapter(jobForms,getApplicationContext());
                                view.setAdapter(jobFormAdapter);
                                view.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                                //jobFormAdapter.UpdateList(jobForms);
                                findViewById(R.id.jobFormsProgressBar).setVisibility(View.INVISIBLE);
                                findViewById(R.id.job_form_list).setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }.start();
                break;
            case 2:
                new Thread(){
                    @Override
                    public void run() {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                List<JobForm> jobForms = _context.GetJobFormsForThisMonth();
                                jobFormAdapter = new FormAdapter(jobForms,getApplicationContext());
                                view.setAdapter(jobFormAdapter);
                                view.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                                //jobFormAdapter.UpdateList(jobForms);
                                findViewById(R.id.jobFormsProgressBar).setVisibility(View.INVISIBLE);
                                findViewById(R.id.job_form_list).setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }.start();
                break;
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
