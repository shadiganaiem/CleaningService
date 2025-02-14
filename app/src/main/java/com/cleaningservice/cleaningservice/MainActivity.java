package com.cleaningservice.cleaningservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

import Authentications.Preferences;

public class MainActivity extends AppCompatActivity {
    private TextView SignUpButton;
    private Button SignInButton;
    private Preferences sp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = new Preferences();
        int userId = sp.GetLoggedInUserID(this);
        if(userId != 0){
            Intent intent = new Intent(this,HomeActivity.class);
            startActivity(intent);
        }

          SignUpButton= findViewById(R.id.SignUpBtn);
          SignInButton= findViewById(R.id.SignInBtn);

    }

    public void OpenRegisterActivity(View v){
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    public void OpenLoginActivity(View v){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }
}
