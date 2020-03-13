package com.cleaningservice.cleaningservice.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.cleaningservice.cleaningservice.ApplicationDbContext;
import com.cleaningservice.cleaningservice.Customer.CustomSpinner.OnSpinnerEventsListener;
import com.cleaningservice.cleaningservice.ProfileActivity;
import com.cleaningservice.cleaningservice.R;
import com.cleaningservice.cleaningservice.Util;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.sql.Array;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Authentications.Preferences;
import Models.JobForm;

import static java.security.AccessController.getContext;

public class FindCleanerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = "";
    private TextView dateText;
    private TextView dateText2;
    private TextView cityText;
    private TextView addressText;
    private String startDate;
    private String endDate;
    private DrawerLayout drawer;
    private int roomNum;
    private float budget;
    private String address;
    private String city;
    private ApplicationDbContext _context = null;



    //Google Places API Client
    private PlacesClient placesClient;

    int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_cleaner);

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

        GooglePlacesApiConnect();
        dateText = findViewById(R.id.date_text);
        dateText2= findViewById(R.id.date_text2);
        addressText = findViewById(R.id.street);
        cityText = findViewById(R.id.city);


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
        month+=1;
        String date =  month +  "/" + dayOfMonth + "/" + year;
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
        String ApiKey = null;
        try {
            ApiKey = Util.GetProperty("api.googleplaces",getApplicationContext());


        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(),ApiKey);
        }
        placesClient = Places.createClient(this);

        final AutocompleteSupportFragment autocompleteSupportFragment=
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        Geocoder geocoder;
        geocoder = new Geocoder(this);

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.LAT_LNG,Place.Field.NAME));
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                final LatLng latLng= place.getLatLng();

             //   Log.i("placesAPI","onPlaceSelected: "+latLng.latitude+"\n"+latLng.longitude);

                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String saddress = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String scity = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();
                    String[] add = saddress.split(",");
                    address=add[0];
                    city=scity;
                    cityText.setText(city);
                    addressText.setText(address);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(@NonNull Status status) {

            }
        });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void Publish(View view) throws ParseException {
        budget = Float.parseFloat(((EditText)findViewById(R.id.budget)).getText().toString());
        roomNum = Integer.parseInt(((EditText)findViewById(R.id.roomNum)).getText().toString());
        startDate = dateText.getText().toString();
        endDate = dateText2.getText().toString();

        int customerId = _context.GetUser(Preferences.GetLoggedInUserID(this)).CustomerId;
        JobForm jobForm = new JobForm(
                customerId,
                roomNum,
                city,
                address,
                budget
        );

        _context.InsertJobForm(jobForm,startDate,endDate);
    }
}


