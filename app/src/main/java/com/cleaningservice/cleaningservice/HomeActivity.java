package com.cleaningservice.cleaningservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;

import Authentications.Preferences;

public class HomeActivity extends AppCompatActivity {

     BottomNavigationView navigation;
     FrameLayout frameLayout;

    private ProfileFragment profilefragment;
    private FindCleanerFragment findcleanerfragment;
    private FavoritesListFragment flistfragment;
    private Preferences sp = null;

    //Google Places API Client
    private PlacesClient placesClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sp = new Preferences();
        if(sp.GetLoggedInUserID(this) == 0){
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        }

        navigation = findViewById(R.id.bottomNav);
        frameLayout = findViewById(R.id.frameLayout);

        profilefragment = new ProfileFragment();
        findcleanerfragment = new FindCleanerFragment();
        flistfragment = new FavoritesListFragment();

        initilizingFragment(findcleanerfragment);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //Switch to select which case is chosen
                switch(menuItem.getItemId()) {
                    case R.id.navigation_profile:
                        initilizingFragment(profilefragment);
                        return true;
                    case R.id.navigation_favlist:
                        initilizingFragment(flistfragment);
                        return true;
                    case R.id.navigation_findcleaner:
                        initilizingFragment(findcleanerfragment);
                        return true;
                }
                return false;
            }
        });
    }

    private void initilizingFragment(Fragment fragment){
        FragmentTransaction fragmenttran = getSupportFragmentManager().beginTransaction();
        fragmenttran.replace(R.id.frameLayout,fragment);
        fragmenttran.commit();
    }



}
