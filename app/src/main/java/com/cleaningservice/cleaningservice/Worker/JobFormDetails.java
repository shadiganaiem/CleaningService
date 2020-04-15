package com.cleaningservice.cleaningservice.Worker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cleaningservice.cleaningservice.ApplicationDbContext;
import com.cleaningservice.cleaningservice.GlideApp;
import com.cleaningservice.cleaningservice.Services.MailService;
import com.cleaningservice.cleaningservice.ProfileActivity;
import com.cleaningservice.cleaningservice.R;
import com.cleaningservice.cleaningservice.Util;
import com.google.android.material.navigation.NavigationView;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import Authentications.Preferences;
import Models.Employee;
import Models.JobForm;
import Models.JobFormRequest;

public class JobFormDetails extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private JobForm jobForm = null;
    private ApplicationDbContext _context = null;
    private ProgressBar formDetailsProgressBar;
    private LinearLayout formComponents;
    private Handler mainhandler = new Handler();

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

        formDetailsProgressBar = findViewById(R.id.formDetailsProgressBar);
        formComponents = findViewById(R.id.formComponents);

        Runnable runnable = new Runnable() {
            Bundle bundle = getIntent().getExtras();
            int jobFormId = 0;
            @Override
            public void run() {
                mainhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (bundle != null) {
                            jobFormId = bundle.getInt("jobFormId");
                            jobForm = _context.GetJobFormById(jobFormId);

                            InitializeViewModel();
                        }
                    }
                });
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

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
            case R.id.navigation_myjobs:
                intent = new Intent(JobFormDetails.this, JobFormRequests.class);
                startActivity(intent);
                break;
            case R.id.navigation_profile:
                intent = new Intent(JobFormDetails.this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.navigation_favlist:
                intent = new Intent(JobFormDetails.this,Favorites.class);
                startActivity(intent);
                break;
        }

        return false;
    }

    private void InitializeViewModel() {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        formDetailsProgressBar.setVisibility(View.VISIBLE);
        formComponents.setVisibility(View.INVISIBLE);

        TextView customerFullName = findViewById(R.id.CustomerFullName);
        TextView customerRating = findViewById(R.id.CustomerRating);
        TextView descirption = (TextView) findViewById(R.id.descriptionBox);
        Button sendRequestBtn = (Button) findViewById(R.id.sendRequestBtn);
        customerFullName.setText(jobForm.customer.Firstname + " " + jobForm.customer.Lastname);
        String rating = "";
        if (jobForm.customer.Rating != 0) {
            rating += "\n";
            for (int i = 0; i < jobForm.customer.Rating && i < 5; i++) {
                rating += "★";
            }
        }

        String details = "";
        details += getResources().getString(R.string.Address) + " : ";
        details += jobForm.City + " - " + jobForm.Address + "\n";
        details += getResources().getString(R.string.RoomsNumber) + " : ";
        details += jobForm.Rooms + " \n";
        details += getResources().getString(R.string.Budget) + " : ";
        details += jobForm.Budget + " ש\"ח " + "\n \n";

        details += getResources().getString(R.string.StartDate) + " : ";
        details += dateFormat.format(jobForm.StartDate) + "\n";
        details += getResources().getString(R.string.EndDate) + " : ";
        details += dateFormat.format(jobForm.EndDate) + "\n";

        if (jobForm.Description != null) {
            details += getResources().getString(R.string.description) + " : ";
            details += jobForm.Description;
        }

        customerRating.setText(rating);
        descirption.setMovementMethod(new ScrollingMovementMethod());
        descirption.setTextSize(20);
        descirption.setText(details);

        int employeeId = _context.GetEmployeeIdByUserID(Preferences.GetLoggedInUserID(getApplicationContext()));
        if (_context.IfJobFormRequested(jobForm.ID, employeeId)) {
            sendRequestBtn.setText(R.string.RequestSent);
            sendRequestBtn.setOnClickListener(null);
        }

        formDetailsProgressBar.setVisibility(View.INVISIBLE);
        formComponents.setVisibility(View.VISIBLE);

        byte[] profileImage = _context.GetProfileImageByCustomerId(jobForm.CustomerId);
        GlideApp.with(getApplicationContext()).load(profileImage).into((CircularImageView) findViewById(R.id.jobFormDetailsProfileImage));


        if (jobForm.AllImagesBytes != null && jobForm.AllImagesBytes.size() > 0) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.image_container);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            for (int i = 0; i < jobForm.AllImagesBytes.size(); i++) {
                byte[] imageBytes = jobForm.AllImagesBytes.get(i);

                layoutParams.setMargins(20, 20, 20, 20);

                layoutParams.gravity = Gravity.CENTER;
                Drawable bitmap = new BitmapDrawable(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));

                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setImageDrawable(bitmap);
                //imageView.setOnClickListener(documentImageListener);
                imageView.setLayoutParams(layoutParams);

                imageView.getLayoutParams().height = 600;
                imageView.getLayoutParams().width = 600;
                imageView.setBackgroundResource(R.drawable.image_border);
                layout.addView(imageView);
            }
        }
    }

    public void SendRequest(View v) {
        formComponents.setVisibility(View.INVISIBLE);
        formDetailsProgressBar.setVisibility(View.VISIBLE);

        int employeeId = _context.GetEmployeeIdByUserID(Preferences.GetLoggedInUserID(getApplicationContext()));
        Employee employee = _context.GetEmployee(employeeId);

        JobFormRequest jobFormRequest = new JobFormRequest(employeeId, jobForm.ID);
        if (_context.InsertJobFormRequest(jobFormRequest)) {

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        String body = "Hi " + jobForm.customer.Firstname + ",\n"
                                + "You have received a new job request From " + employee.Firstname + " " + employee.Lastname
                                + ".\n" + "For your JobForm at " + jobForm.City + " " + jobForm.Address + "\n\n\n"
                                + "The request will be rejected if no reply is made within 48 Hours !\n"
                                + "-------------------\n"
                                + "Best Regards, CleaningService Team.\n"
                                + "For more information please Contact us at \n support@cleaningService.com";

                        String recipients = jobForm.customer.Email;
                        String email = Util.GetProperty("mail.email", getApplicationContext());
                        String password = Util.GetProperty("mail.password", getApplicationContext());
                        MailService _mailService = new MailService(email, password);
                        _mailService.sendMail("CleaningService: NEW JOB REQUEST",
                                body,
                                "CleaningService",
                                recipients);


                        body = "Hi " + employee.Firstname + ",\n"
                                + "Your job request has been sent. The request will be rejected if no reply is made within 48 Hours !\n"
                                + "Job Details : \n"
                                + "Customer name : " + jobForm.customer.Firstname + " " + jobForm.customer.Lastname + ".\n"
                                + "Address : " + jobForm.City + " " + jobForm.Address + ".\n\n\n"
                                + "-------------------\n"
                                + "Best Regards, CleaningService Team.\n"
                                + "For more information please Contact us at \n support@cleaningService.com";

                        recipients = employee.Email;
                        email = Util.GetProperty("mail.email", getApplicationContext());
                        password = Util.GetProperty("mail.password", getApplicationContext());
                        _mailService = new MailService(email, password);
                        _mailService.sendMail("CleaningService: NEW JOB REQUEST",
                                body,
                                "CleaningService",
                                recipients);

                    } catch (Exception ex) {

                    }
                }
            });

            Intent intent = new Intent(JobFormDetails.this, Home.class);
            startActivity(intent);
        } else {

        }
    }

    public void BackToHome(View v) {
        Intent intent = new Intent(JobFormDetails.this, Home.class);
        startActivity(intent);
    }
}
