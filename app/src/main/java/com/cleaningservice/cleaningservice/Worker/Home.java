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
import android.view.MenuItem;

import com.cleaningservice.cleaningservice.ApplicationDbContext;
import com.cleaningservice.cleaningservice.R;
import com.google.android.material.navigation.NavigationView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import Models.JobForm;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private ApplicationDbContext _context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        RecyclerView view = findViewById(R.id.job_form_list);
        synchronized (this) {
            List<JobForm> jobForms = _context.GetJobForms();
            view.setAdapter(new FormAdapter(jobForms, getApplicationContext()));
            view.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        }
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

        }
        return false;
    }

}
