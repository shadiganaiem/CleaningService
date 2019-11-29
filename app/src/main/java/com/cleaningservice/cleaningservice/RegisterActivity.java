package com.cleaningservice.cleaningservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewStructure;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;

public class RegisterActivity extends AppCompatActivity {
    private LinearLayout FirstStep;
    private LinearLayout SecondStep;
    private ApplicationDbContext _context = null;
    private Validator _validator = null;

    //Customer Details
    private TextInputEditText Firstname;
    private TextInputEditText Lastname;
    private TextInputEditText Email;
    private TextInputEditText Phone;
    private TextInputEditText City;
    private TextInputEditText Address;

    //User Details
    private TextInputEditText Username;
    private TextInputEditText Password;
    private TextInputEditText RePassword;

    //Google Places API Client
    PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirstStep = findViewById(R.id.FirstStep);
        SecondStep = findViewById(R.id.SecondStep);

        Firstname = findViewById(R.id.Firstname);
        Lastname = findViewById(R.id.Lastname);
        Email = findViewById(R.id.Email);
        Phone= findViewById(R.id.Phone);
        City= findViewById(R.id.City);
        Address= findViewById(R.id.Address);
        Username = findViewById(R.id.Username);
        Password = findViewById(R.id.Password);
        RePassword = findViewById(R.id.RePassword);

        _context = new ApplicationDbContext();
        _validator = new Validator();
        GooglePlacesApiConnect();
    }

    //
    public void NextStepRegister(View v) {


        String regex = "^[\\p{L} ]+$";
        boolean status = true;

        if(!_validator.InputValidate(Firstname,regex))
            status = false;
        if(!_validator.InputValidate(Lastname,regex))
            status = false;
        if(!_validator.InputValidate(City,regex))
            status = false;
        if(!_validator.InputValidate(Address,regex))
            status = false;
        if(GetInputText(Phone).length() != 10){
            android.text.Spanned errorMsg  = Html.fromHtml("<font color='white'>מספר נייד לא תקין</font>");
            Phone.setError(errorMsg);
            status = false;
        }
        regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if(!_validator.InputValidate(Email,regex) || GetInputText(Email).equals("")){
            android.text.Spanned errorMsg  = Html.fromHtml("<font color='white'>דואר אלקטרוני אינו תקין</font>");
            Email.setError(errorMsg);
            status = false;
        }
        if(status){
            FirstStep.setVisibility(View.INVISIBLE);
            SecondStep.setVisibility(View.VISIBLE);
        }
    }

    public void PreviousStepRegister(View v) {
        FirstStep.setVisibility(View.VISIBLE);
        SecondStep.setVisibility(View.INVISIBLE);
    }

    public void AddUser(View v) {

        String regex = "^[\\p{L}0-9_]+$";
        boolean status = true;

        if(!_validator.InputValidate(Username,regex)){
            status = false;
        }
        if(!GetInputText(Password).equals(GetInputText(RePassword)) && !GetInputText(Password).equals("")){
            android.text.Spanned errorMsg = Html.fromHtml("<font color='white'>סיסמאות לא תואמות</font>");
            Password.setError(errorMsg);
            RePassword.setError(errorMsg);
            status = false;
        }
        if (status){
            String query = "INSERT INTO Customers(Firstname,Lastname,Email,Phone,City,Address) "+
                "VALUES('"+ GetInputText(Firstname) +"','"+GetInputText(Lastname)+"','"+GetInputText(Email)+
                    "','"+GetInputText(Phone)+"','"+GetInputText(City)+"','"+GetInputText(Address)+"');"+
                "INSERT INTO Users(Username,CustomerId,Password,Status) " + "SELECT '"+GetInputText(Username)+"',MAX(ID),'"+GetInputText(Password)+"','1' " +
                "FROM Customers WHERE Customers.Phone = '"+GetInputText(Phone)+"';";

            _context.ExecuteInsertData(query);
            Intent intent = new Intent(this,LoginActivity.class);
            intent.putExtra("flag", true);
            startActivity(intent);

        }
    }



    /**
     * @param editText
     * @return InputValue
     */
    public String GetInputText(TextInputEditText editText){
        return editText.getText().toString();
    }


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

}