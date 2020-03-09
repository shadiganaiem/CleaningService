package com.cleaningservice.cleaningservice.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.cleaningservice.cleaningservice.ProfileActivity;
import com.cleaningservice.cleaningservice.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.Calendar;

public class FindCleanerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DatePickerDialog.OnDateSetListener {

    private TextView dateText;
    private TextView dateText2;
    private String startDate;
    private String endDate;
    private DrawerLayout drawer;
    private int roomNum;
    private float budget;
    private String adress;



    //Google Places API Client
    private PlacesClient placesClient;

    int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_cleaner);

        Toolbar toolbar2 = findViewById(R.id.sidebar);
        setSupportActionBar(toolbar2);

        drawer=findViewById(R.id.drawer_layout);
        NavigationView nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar2,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        GooglePlacesApiConnect();
        dateText = findViewById(R.id.date_text);
        dateText2= findViewById(R.id.date_text2);

        findViewById(R.id.show_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        findViewById(R.id.show_dialog2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog2();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.navigation_profile:
                Intent intent = new Intent(FindCleanerActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.navigation_favlist:
                Intent intent2 = new Intent(FindCleanerActivity.this, FavoritesListActivity.class);
                startActivity(intent2);
                break;
            case R.id.navigation_findcleaner:
                break;
            case R.id.navigation_myjobs:
                Intent intent4 = new Intent(FindCleanerActivity.this, MyJobsActivity.class);
                startActivity(intent4);
                break;
            case R.id.navigation_notifications:
                Intent intent5 = new Intent(FindCleanerActivity.this, NotificationsActivity.class);
                startActivity(intent5);
                break;
        }
        return false;
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




    public void showDatePickerDialog(){
        flag=0;
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }


    public void showDatePickerDialog2(){
        flag=1;
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date =  dayOfMonth + "/" + month + "/" + year;
        if(flag==0) {
            dateText.setText(date);
        }
        else{
            dateText2.setText(date);
        }
    }

    /**
     * Google Places API connect and initialize
     */
    public void GooglePlacesApiConnect(){
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


    public void Publish(View view) {
        roomNum = Integer.parseInt(findViewById(R.id.roomNum).toString());
        budget = Float.parseFloat(findViewById(R.id.LoginPassword).toString());
        adress = findViewById(R.id.autocomplete_fragment).toString();
        startDate = dateText.toString();
        endDate = dateText2.toString();

    }
}


