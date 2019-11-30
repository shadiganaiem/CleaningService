package com.cleaningservice.cleaningservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
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

    //Database context
    private ApplicationDbContext _context = null;

    //Inputs validator class
    private Validator _validator = null;

    //Registration steps layouts
    private LinearLayout FirstStep;
    private LinearLayout SecondStep;

    //User input Details
    private TextInputEditText Firstname;
    private TextInputEditText Lastname;
    private TextInputEditText Email;
    private TextInputEditText Phone;
    private TextInputEditText Username;
    private TextInputEditText Password;
    private TextInputEditText RePassword;

    //Switch
    private Switch IsEmployee;

    //Google Places API Client
    PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        _context = new ApplicationDbContext();
        _validator = new Validator();
        GooglePlacesApiConnect();

        //layouts
        FirstStep = findViewById(R.id.FirstStep);
        SecondStep = findViewById(R.id.SecondStep);
        //User inputs
        Firstname = findViewById(R.id.Firstname);
        Lastname = findViewById(R.id.Lastname);
        Email = findViewById(R.id.Email);
        Phone= findViewById(R.id.Phone);
        Username = findViewById(R.id.Username);
        Password = findViewById(R.id.Password);
        RePassword = findViewById(R.id.RePassword);
        //user type
        IsEmployee = findViewById(R.id.Type);
    }

    /**
     * Validate all Inputs And get the next register step
     * @param v
     */
    public void NextStepRegister(View v) {

        String regex = "^[\\p{L} ]+$";
        boolean status = true;

        if(!_validator.InputValidate(Firstname,regex))
            status = false;
        if(!_validator.InputValidate(Lastname,regex))
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

    /**
     * back to first register step
     * @param v
     */
    public void PreviousStepRegister(View v) {
        FirstStep.setVisibility(View.VISIBLE);
        SecondStep.setVisibility(View.INVISIBLE);
    }

    /**
     * add User to Database
     * @param v
     */
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
            String table  = IsEmployee.isChecked() ?  "Employees" : "Customers";
            String tableId = IsEmployee.isChecked() ? "EmployeeId" : "CustomerId";

            String query = "INSERT INTO " + table + "(Firstname,Lastname,Email,Phone) "+
                "VALUES('"+ GetInputText(Firstname) +"','"+GetInputText(Lastname)+"','"+GetInputText(Email)+
                    "','"+GetInputText(Phone)+"');"+
                "INSERT INTO Users(Username,"+ tableId +",Password,Status) " + "SELECT '"+GetInputText(Username)+ "',MAX(ID),'"+GetInputText(Password)+"','1' " +
                "FROM "+ table +" WHERE +"+table+".Phone = '"+GetInputText(Phone)+"';";

            if(_context.ExecuteInsertData(query)){
                Intent intent = new Intent(this,LoginActivity.class);
                intent.putExtra("flag", true);
                startActivity(intent);
            }
            else{
                Toast.makeText(getApplicationContext(),"אירעה שגיאה, נא לנסות מאוחר יותר", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * get input text string
     * @param editText
     * @return InputValue
     */
    public String GetInputText(TextInputEditText editText){
        return editText.getText().toString();
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
}