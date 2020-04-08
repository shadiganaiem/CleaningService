package com.cleaningservice.cleaningservice.Worker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.List;

import Authentications.Preferences;
import Models.JobForm;
import Models.JobFormRequest;

public class JobFormRequests extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private RequestAdapter requestAdapter;
    private ApplicationDbContext _context = null;
    private List<JobFormRequest> requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_form_requests);


        try {
            _context = ApplicationDbContext.getInstance(getApplicationContext());
        } catch (SQLException e) {
            e.printStackTrace();
        }


        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView = findViewById(R.id.job_form_list);
                findViewById(R.id.jobFormsProgressBar).setVisibility(View.VISIBLE);
                findViewById(R.id.job_form_list).setVisibility(View.INVISIBLE);

                int userId = Preferences.GetLoggedInUserID(getApplicationContext());
                int employeeId = _context.GetEmployeeIdByUserID(userId);
                requests = _context.GetEmployeeJobRequests(employeeId);
                requestAdapter = new RequestAdapter(requests, getApplicationContext());

                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(requestAdapter);

                findViewById(R.id.jobFormsProgressBar).setVisibility(View.INVISIBLE);
                findViewById(R.id.job_form_list).setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Intent intent;
        switch(menuItem.getItemId()) {
            case R.id.navigation_employee_home:
                intent = new Intent(JobFormRequests.this, Home.class);
                startActivity(intent);
                break;
            case R.id.navigation_make_worker_card:
                intent = new Intent(JobFormRequests.this, SetWorkingDetailsActivity.class);
                startActivity(intent);
                break;
            case R.id.navigation_profile:
                intent = new Intent(JobFormRequests.this, ProfileActivity.class);
                startActivity(intent);
                break;
        }

        return false;
    }
}
