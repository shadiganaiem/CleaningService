package com.cleaningservice.cleaningservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private Button SignUpButton;
    private Button SignInButton;
    private Button HomeButton;

    PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SignUpButton= findViewById(R.id.SignUpBtn);
        SignInButton= findViewById(R.id.SignInBtn);
        HomeButton= findViewById(R.id.HomeBtn);
        HomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenHomeActivity();
            }
        });

         String ApiKey = "AIzaSyBtl61YpGjZBArHe_7h9XUjXwdfYlcAT-Y";

         if(!Places.isInitialized()){
             Places.initialize(getApplicationContext(),ApiKey);
         }
         placesClient = Places.createClient(this);

         final AutocompleteSupportFragment autocompleteSupportFragment=
                 (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

         autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.LAT_LNG,Place.Field.NAME));
         autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
             @Override
             public void onPlaceSelected(@NonNull Place place) {
                 final LatLng latLng= place.getLatLng();
                 Log.i("placesAPI","onPlaceSelected: "+latLng.latitude+"\n"+latLng.longitude);
             }

             @Override
             public void onError(@NonNull Status status) {

             }
         });

    }

    public void OpenHomeActivity(){
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
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
