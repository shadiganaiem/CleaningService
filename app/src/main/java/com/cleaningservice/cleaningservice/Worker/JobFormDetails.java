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
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cleaningservice.cleaningservice.ApplicationDbContext;
import com.cleaningservice.cleaningservice.GlideApp;
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

public class JobFormDetails extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private JobForm jobForm = null;
    private ApplicationDbContext _context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_form_details);

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

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = getIntent().getExtras();
                int jobFormId = 0;
                if (bundle != null) {
                    jobFormId = bundle.getInt("jobFormId");
                    jobForm = _context.GetJobFormById(jobFormId);
                    InitializeViewModel();
                }

            }
        });
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
                intent = new Intent(JobFormDetails.this, Home.class);
                startActivity(intent);
                break;
            case R.id.navigation_make_worker_card:
                intent = new Intent(JobFormDetails.this, SetWorkingDetailsActivity.class);
                startActivity(intent);
                break;
            case R.id.navigation_profile:
                intent = new Intent(JobFormDetails.this, ProfileActivity.class);
                startActivity(intent);
                break;
        }

        return false;
    }

    private  void InitializeViewModel() {
        TextView customerFullName = findViewById(R.id.CustomerFullName);
        TextView customerRating = findViewById(R.id.CustomerRating);
        customerFullName.setText(jobForm.customer.Firstname + " " + jobForm.customer.Lastname);
        String rating = "";
        if (jobForm.customer.Rating != 0) {
            rating += "\n";
            for (int i = 0; i < jobForm.customer.Rating && i < 5; i++) {
                rating += "★";
            }
        }
        customerRating.setText(rating);

        if (jobForm.AllImagesBytes != null &&  jobForm.AllImagesBytes.size() > 0) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                    byte[] profileImage = _context.GetProfileImageByCustomerId(jobForm.CustomerId);
                    GlideApp.with(getApplicationContext()).load(profileImage).into((ImageView)findViewById(R.id.jobFormDetailsProfileImage));
                    LinearLayout layout = (LinearLayout) findViewById(R.id.formImagesSection);

                    for (int i = 0; i < jobForm.AllImagesBytes.size(); i++) {
                        ImageView imageView = new ImageView(getApplicationContext());
                        imageView.setId(i);
                        imageView.setPadding(2, 2, 2, 2);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        layout.addView(imageView);


                        byte[] imageBytes = jobForm.AllImagesBytes.get(i);
                        Drawable bitmap = new BitmapDrawable(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
                        imageView.setImageDrawable(bitmap);
                    }
                }
            });
        }
    }
}
