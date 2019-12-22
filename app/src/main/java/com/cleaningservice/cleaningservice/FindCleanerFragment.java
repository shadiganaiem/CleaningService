package com.cleaningservice.cleaningservice;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindCleanerFragment extends Fragment {
    private PlacesClient placesClient;


    public FindCleanerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        GooglePlacesApiConnect();
        return inflater.inflate(R.layout.fragment_find_cleaner, container, false);
    }

    /**
     * Google Places API connect and initialize
     */
    public void GooglePlacesApiConnect(){
        String ApiKey = "AIzaSyBtl61YpGjZBArHe_7h9XUjXwdfYlcAT-Y";

        Activity activity = getActivity();
        Context context = activity.getApplicationContext();

        if(!Places.isInitialized()){
            Places.initialize(context,ApiKey);
        }
        placesClient = Places.createClient(context);

        final AutocompleteSupportFragment autocompleteSupportFragment=
                (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
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
}
