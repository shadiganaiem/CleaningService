package com.cleaningservice.cleaningservice.Worker;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cleaningservice.cleaningservice.ApplicationDbContext;
import com.cleaningservice.cleaningservice.MainActivity;
import com.cleaningservice.cleaningservice.R;
import com.cleaningservice.cleaningservice.Util;
import com.cleaningservice.cleaningservice.Worker.FormAdapter.OnJobFormListiner;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import Authentications.Preferences;
import Models.JobForm;

public class Home extends AppCompatActivity implements OnJobFormListiner , NavigationView.OnNavigationItemSelectedListener,
        TabLayout.OnTabSelectedListener{

    private int tabSelected = 0;
    private int minRateSelected = 0;
    private int maxRateSelected = 5;
    private String city = "";
    private DrawerLayout drawer;
    private TabLayout tabLayout;
    private Dictionary ratingDict;
    private Spinner ratingSpinner;
    private FormAdapter jobFormAdapter;
    private ApplicationDbContext _context = null;
    private List<JobForm> jobForms;
    private Handler mainhandler = new Handler();
    private RecyclerView view;
    //Google Places API Client
    private PlacesClient placesClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_employee_home);

       try {
            _context = ApplicationDbContext.getInstance(getApplicationContext());
           GooglePlacesApiConnect();
        } catch (Exception e) {
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

        //Tabs Layout
        tabLayout = findViewById(R.id.TabLayout);
        tabLayout.setOnTabSelectedListener(this);

        // Initializing a Dictionary
        ratingDict = new Hashtable();
        // put() method
        ratingDict.put(0, "0 5");
        ratingDict.put(1, "0 2");
        ratingDict.put(2, "2 4");
        ratingDict.put(3, "3 5");

        ratingSpinner = findViewById(R.id.RatingSpinner);
        ratingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                RecyclerView recyclerView = findViewById(R.id.job_form_list);
                findViewById(R.id.jobFormsProgressBar).setVisibility(View.VISIBLE);
                findViewById(R.id.job_form_list).setVisibility(View.INVISIBLE);
                /** Spinner Value gives The rating range that user had selected */
                String rating = ratingDict.get(position).toString();
                String[] splittedRating = rating.split(" ");
                /** Get min and max rate range */
                minRateSelected = Integer.parseInt(splittedRating[0]);
                maxRateSelected = Integer.parseInt(splittedRating[1]);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        jobForms = _context.GetJobFormsByPublisherRating(minRateSelected, maxRateSelected, tabSelected);
                        mainhandler.post(new Runnable() {
                            @Override
                            public void run() {
                                jobFormAdapter = new FormAdapter(jobForms, Home.this, Home.this);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                recyclerView.setAdapter(jobFormAdapter);
                                findViewById(R.id.jobFormsProgressBar).setVisibility(View.INVISIBLE);
                                findViewById(R.id.job_form_list).setVisibility(View.VISIBLE);
                            }
                        });
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            }


            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

       /* RecyclerView view = findViewById(R.id.job_form_list);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                jobForms = _context.GetJobForms(minRateSelected, maxRateSelected);
                jobFormAdapter = new FormAdapter(jobForms, getApplicationContext(), Home.this);
                view.setAdapter(jobFormAdapter);
                view.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                findViewById(R.id.jobFormsProgressBar).setVisibility(View.INVISIBLE);
                findViewById(R.id.job_form_list).setVisibility(View.VISIBLE);
            }});*/
    }

    private void searchPlace(){
        RecyclerView recyclerView = findViewById(R.id.job_form_list);
        findViewById(R.id.jobFormsProgressBar).setVisibility(View.VISIBLE);
        findViewById(R.id.job_form_list).setVisibility(View.INVISIBLE);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                jobForms = _context.GetJobForms("",minRateSelected, maxRateSelected);
                mainhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        jobFormAdapter = new FormAdapter(jobForms, Home.this, Home.this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.setAdapter(jobFormAdapter);
                        findViewById(R.id.jobFormsProgressBar).setVisibility(View.INVISIBLE);
                        findViewById(R.id.job_form_list).setVisibility(View.VISIBLE);

                    }
                });
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
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
     * Google Places API connect and initialize
     */
    public void GooglePlacesApiConnect() throws IOException {
        String ApiKey = Util.GetProperty("api.googleplaces",getApplicationContext());

        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(),ApiKey);
        }
        placesClient = Places.createClient(this);

        final AutocompleteSupportFragment autocompleteSupportFragment=
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        Geocoder geocoder;
        geocoder = new Geocoder(this);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.LAT_LNG,Place.Field.NAME));

        // on clearing the search
        autocompleteSupportFragment.getView().findViewById(R.id.places_autocomplete_clear_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        autocompleteSupportFragment.setText("");
                        view.setVisibility(View.GONE);
                        searchPlace();
                    }
        });
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                final LatLng latLng= place.getLatLng();
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String scity = addresses.get(0).getLocality();
                    city=scity;

                    RecyclerView recyclerView = findViewById(R.id.job_form_list);
                    findViewById(R.id.jobFormsProgressBar).setVisibility(View.VISIBLE);
                    findViewById(R.id.job_form_list).setVisibility(View.INVISIBLE);
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            jobForms = _context.GetJobForms(city,minRateSelected, maxRateSelected);
                            mainhandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    jobFormAdapter = new FormAdapter(jobForms, Home.this, Home.this);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                    recyclerView.setAdapter(jobFormAdapter);
                                    findViewById(R.id.jobFormsProgressBar).setVisibility(View.INVISIBLE);
                                    findViewById(R.id.job_form_list).setVisibility(View.VISIBLE);

                                }
                            });
                        }
                    };
                    Thread thread = new Thread(runnable);
                    thread.start();

                        } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(@NonNull Status status) {
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
                intent = new Intent(Home.this, Home.class);
                startActivity(intent);
                break;
            case R.id.navigation_profile:
                intent = new Intent(Home.this, WorkerProfile.class);
                startActivity(intent);
                break;
            case R.id.navigation_myjobs:
                intent = new Intent(Home.this,JobFormRequests.class);
                startActivity(intent);
                break;
            case R.id.navigation_favlist:
                intent = new Intent(Home.this,Favorites.class);
                startActivity(intent);
                break;
            case R.id.navigation_signout:
                Preferences.Logout(this);
                Intent intent6 = new Intent(Home.this, MainActivity.class);
                startActivity(intent6);
                break;
            case R.id.navigation_jobOffers:
                intent = new Intent(Home.this,JobProposals.class);
                startActivity(intent);
                break;
            case R.id.change_language:
                showLanguageDialog();
                break;
        }
        return false;
    }

    private void showLanguageDialog() {
        final String[] languagesList = { "العربية", "עברית" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.chooseLanguage));
        builder.setSingleChoiceItems(languagesList, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0) {
                    setLocale("ar");
                    recreate();
                }
                else if (which ==1){
                    setLocale("he");
                    recreate();
                }
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void setLocale(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = prefs.getString("My_Lang","");
        setLocale(lang);
    }

    /**
     * On Tabs Layout Select , Get the relevant jobforms list.
     * @param tab
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mainhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        view = findViewById(R.id.job_form_list);
                        findViewById(R.id.jobFormsProgressBar).setVisibility(View.VISIBLE);
                        findViewById(R.id.job_form_list).setVisibility(View.INVISIBLE);
                    }
                });
                        switch (tab.getPosition()) {
                            case 0:
                                tabSelected = 0;
                                jobForms = _context.GetJobForms(city,minRateSelected, maxRateSelected);
                                jobFormAdapter = new FormAdapter(jobForms,Home.this, Home.this);
                                mainhandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                    view.setAdapter(jobFormAdapter);
                                    view.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                                    findViewById(R.id.jobFormsProgressBar).setVisibility(View.INVISIBLE);
                                    findViewById(R.id.job_form_list).setVisibility(View.VISIBLE);

                                    }
                                });
                                break;
                            case 1:
                                tabSelected = 1;
                                jobForms = _context.GetJobFormsForThisWeek(minRateSelected, maxRateSelected);
                                mainhandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        jobFormAdapter = new FormAdapter(jobForms, Home.this, Home.this);
                                        view.setAdapter(jobFormAdapter);
                                        view.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                                        findViewById(R.id.jobFormsProgressBar).setVisibility(View.INVISIBLE);
                                        findViewById(R.id.job_form_list).setVisibility(View.VISIBLE);
                                    }
                                });
                                break;
                            case 2:
                                tabSelected = 2;
                                        jobForms = _context.GetJobFormsForThisMonth(minRateSelected, maxRateSelected);
                                mainhandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        jobFormAdapter = new FormAdapter(jobForms, Home.this, Home.this);
                                        view.setAdapter(jobFormAdapter);
                                        view.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                                        findViewById(R.id.jobFormsProgressBar).setVisibility(View.INVISIBLE);
                                        findViewById(R.id.job_form_list).setVisibility(View.VISIBLE);
                                    }
                                });
                                break;
                        }

            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    @Override
    public void onJobFormClick(int position) {
        int jobFormId = jobForms.get(position).ID;

        Intent intent = new Intent(this,JobFormDetails.class);

        //Pass JobForm Id to JobFormDetails Activity
        Bundle bundle = new Bundle();
        bundle.putInt("jobFormId",jobFormId);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
