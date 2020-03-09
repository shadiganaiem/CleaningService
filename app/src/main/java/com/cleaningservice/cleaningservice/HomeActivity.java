package com.cleaningservice.cleaningservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;


import com.cleaningservice.cleaningservice.Customer.FindCleanerActivity;
import com.cleaningservice.cleaningservice.Worker.Home;
import com.cleaningservice.cleaningservice.Worker.SetWorkingDetailsActivity;

import static Authentications.Preferences.isCustomer;
import static android.app.PendingIntent.getActivity;

public class HomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if(isCustomer(this)) {
            Intent intent = new Intent(HomeActivity.this, FindCleanerActivity.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(HomeActivity.this, Home.class);
            startActivity(intent);
        }

    }




}
