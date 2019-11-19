package com.cleaningservice.cleaningservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.icu.text.Replaceable;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.validation.Validator;

public class RegisterActivity extends AppCompatActivity {
    private ConstraintLayout FirstStep;
    private ConstraintLayout SecondStep;
    private ApplicationDbContext _context = null;

    //Customer Details
    private EditText Firstname;
    private EditText Lastname;
    private EditText Email;
    private EditText Phone;
    private EditText City;
    private EditText Address;

    //User Details
    private EditText Username;
    private EditText Password;
    private EditText RePassword;

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

    }

    //
    public void NextStepRegister(View v) {


        String regex = "^[\\p{L} ]+$";
        boolean status = true;

        if(!InputValidate(Firstname,regex) || !InputValidate(Lastname,regex) || !InputValidate(City,regex) || !InputValidate(Address,regex)){
            status = false;
        }
        if(GetInputText(Phone).length() < 10){
            android.text.Spanned errorMsg  = Html.fromHtml("<font color='white'>מספר נייד לא תקין</font>");
            Phone.setError(errorMsg);
            status = false;
        }
        regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if(!InputValidate(Email,regex) || GetInputText(Email).equals("")){
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

        //if(GetInputText(Username).contains(" ")){
        if(!InputValidate(Username,regex)){
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

            _context.ExecuteQuery(query);
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
    }


    /**
     * Validate text and get a relevant error message
     * @param editText : input EditText to validate
     */
    public boolean InputValidate(EditText editText,String regularExpression){
        android.text.Spanned errorMsg = Html.fromHtml("<font color='white'>שדה חובה</font>");
        Pattern pattern = Pattern.compile(regularExpression);

        String text =GetInputText(editText);

        //Validate Text and get a relevant Error message
        if(text.equals("")){
            editText.setError(errorMsg);
            return false;
        }
        else if(!pattern.matcher(text).matches()){
            errorMsg = Html.fromHtml("<font color='white'>אין להכיל סימנים</font>");
            editText.setError(errorMsg);
            return false;
        }
        return true;
    }

    /**
     * @param editText
     * @return InputValue
     */
    public String GetInputText(EditText editText){
        return editText.getText().toString();
    }

    /*
    public void WarningToastShow(String msg) {

        LayoutInflater li = getLayoutInflater();
        View layout = li.inflate(R.layout.toast,(ViewGroup)findViewById(R.id.CustomToast));
        TextView text = layout.findViewById(R.id.warningText);
        text.setText(msg);
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
        toast.setView(layout);
        toast.show();
    }*/


}