package com.cleaningservice.cleaningservice.Worker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cleaningservice.cleaningservice.ApplicationDbContext;
import com.cleaningservice.cleaningservice.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

import Authentications.Preferences;
import Models.JobFormRequest;

public class JobProposals  extends AppCompatActivity implements JobOffersAdapter.OnJobProposalListener, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private JobOffersAdapter jobOffersAdapter;
    private ApplicationDbContext _context = null;
    private List<JobFormRequest> requests;
    private TabLayout tabLayout;
    private Handler mainhandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_proposals);

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

        RecyclerView recyclerView = findViewById(R.id.employee_job_offers_list);
        findViewById(R.id.jobOfferProgressBar).setVisibility(View.VISIBLE);
        findViewById(R.id.employee_job_offers_list).setVisibility(View.INVISIBLE);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int userId = Preferences.GetLoggedInUserID(getApplicationContext());
                int employeeId = _context.GetEmployeeIdByUserID(userId);
                requests = _context.GetEmployeeJobProposals(employeeId);
                requests.sort(Comparator.comparing(JobFormRequest::GetCreaionDate).reversed());

                mainhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        jobOffersAdapter = new JobOffersAdapter(requests, getApplicationContext(),JobProposals.this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.setAdapter(jobOffersAdapter);

                        findViewById(R.id.jobOfferProgressBar).setVisibility(View.INVISIBLE);
                        findViewById(R.id.employee_job_offers_list).setVisibility(View.VISIBLE);
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
                intent = new Intent(JobProposals.this, Home.class);
                startActivity(intent);
                break;
            case R.id.navigation_profile:
                intent = new Intent(JobProposals.this, WorkerProfile.class);
                startActivity(intent);
                break;
            case R.id.navigation_myjobs:
                intent = new Intent(JobProposals.this,JobFormRequests.class);
                startActivity(intent);
                break;
            case R.id.navigation_favlist:
                intent = new Intent(JobProposals.this,Favorites.class);
                startActivity(intent);
                break;
            case R.id.navigation_jobOffers:
                intent = new Intent(JobProposals.this,JobProposals.class);
                startActivity(intent);
                break;
        }

        return false;
    }

    @Override
    public void onJobProposalClick(int position)
    {
        int jobFormId = requests.get(position).JobFormId;

        Intent intent = new Intent(this,JobFormDetails.class);

        //Pass JobForm Id to JobFormDetails Activity
        Bundle bundle = new Bundle();
        bundle.putInt("jobFormId",jobFormId);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
