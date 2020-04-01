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
import android.widget.AdapterView;
import android.widget.Spinner;

import com.cleaningservice.cleaningservice.ApplicationDbContext;
import com.cleaningservice.cleaningservice.ProfileActivity;
import com.cleaningservice.cleaningservice.R;
import com.cleaningservice.cleaningservice.Worker.FormAdapter.OnJobFormListiner;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.sql.SQLException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import Models.JobForm;

public class Home extends AppCompatActivity implements OnJobFormListiner , NavigationView.OnNavigationItemSelectedListener,
        TabLayout.OnTabSelectedListener{

    private int tabSelected = 0;
    private int minRateSelected = 0;
    private int maxRateSelected = 5;
    private DrawerLayout drawer;
    private TabLayout tabLayout;
    private Dictionary ratingDict;
    private Spinner ratingSpinner;
    private FormAdapter jobFormAdapter;
    private ApplicationDbContext _context = null;
    private List<JobForm> jobForms;

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

        //Tabs Layout
        tabLayout = findViewById(R.id.TabLayout);
        tabLayout.setOnTabSelectedListener(this);

        // Initializing a Dictionary
        ratingDict = new Hashtable();
        // put() method
        ratingDict.put(0, "0 5");
        ratingDict.put(1, "0 2");
        ratingDict.put(2, "2 4");
        ratingDict.put(3, "3 5");

        ratingSpinner = findViewById(R.id.RatingSpinner);
        ratingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                /** Recycler View*/
                RecyclerView recyclerView = findViewById(R.id.job_form_list);
                findViewById(R.id.jobFormsProgressBar).setVisibility(View.VISIBLE);
                findViewById(R.id.job_form_list).setVisibility(View.INVISIBLE);
                /** Spinner Value gives The rating range that user had selected */
                String rating = ratingDict.get(position).toString();
                String[] splittedRating = rating.split(" ");
                /** Get min and max rate range */
                minRateSelected = Integer.parseInt(splittedRating[0]);
                maxRateSelected = Integer.parseInt(splittedRating[1]);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        jobForms = _context.GetJobFormsByPublisherRating(minRateSelected,maxRateSelected,tabSelected);
                        jobFormAdapter = new FormAdapter(jobForms,getApplicationContext(),Home.this);
                        recyclerView.setAdapter(jobFormAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                        findViewById(R.id.jobFormsProgressBar).setVisibility(View.INVISIBLE);
                        findViewById(R.id.job_form_list).setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        RecyclerView view = findViewById(R.id.job_form_list);
        new Thread(){
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        jobForms = _context.GetJobForms(minRateSelected,maxRateSelected);
                        jobFormAdapter = new FormAdapter(jobForms,getApplicationContext(),Home.this);
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

    /**
     * On Tabs Layout Select , Get the relevant jobforms list.
     * @param tab
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        RecyclerView view = findViewById(R.id.job_form_list);
        findViewById(R.id.jobFormsProgressBar).setVisibility(View.VISIBLE);
        findViewById(R.id.job_form_list).setVisibility(View.INVISIBLE);
        switch (tab.getPosition()){
            case 0:
                tabSelected= 0;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                jobForms = _context.GetJobForms(minRateSelected,maxRateSelected);
                                jobFormAdapter = new FormAdapter(jobForms,getApplicationContext(),Home.this);
                                view.setAdapter(jobFormAdapter);
                                view.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                                findViewById(R.id.jobFormsProgressBar).setVisibility(View.INVISIBLE);
                                findViewById(R.id.job_form_list).setVisibility(View.VISIBLE);
                            }
                        });
                break;
            case 1:
                tabSelected = 1;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                jobForms = _context.GetJobFormsForThisWeek(minRateSelected,maxRateSelected);
                                jobFormAdapter = new FormAdapter(jobForms,getApplicationContext(),Home.this);
                                view.setAdapter(jobFormAdapter);
                                view.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                                findViewById(R.id.jobFormsProgressBar).setVisibility(View.INVISIBLE);
                                findViewById(R.id.job_form_list).setVisibility(View.VISIBLE);
                            }
                        });
                break;
            case 2:
                tabSelected = 2;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                jobForms = _context.GetJobFormsForThisMonth(minRateSelected,maxRateSelected);
                                jobFormAdapter = new FormAdapter(jobForms,getApplicationContext(),Home.this);
                                view.setAdapter(jobFormAdapter);
                                view.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                                findViewById(R.id.jobFormsProgressBar).setVisibility(View.INVISIBLE);
                                findViewById(R.id.job_form_list).setVisibility(View.VISIBLE);
                            }
                        });
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    @Override
    public void onJobFormClick(int position) {
        int jobFormId = jobForms.get(position).ID;

        Intent intent = new Intent(this,JobFormDetails.class);

        //Pass JobForm Id to JobFormDetails Activity
        Bundle bundle = new Bundle();
        bundle.putInt("jobFormId",jobFormId);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
