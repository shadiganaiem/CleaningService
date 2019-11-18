package com.cleaningservice.cleaningservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.Connection;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.validation.Validator;

public class RegisterActivity extends AppCompatActivity {
    private ConstraintLayout FirstStep;
    private ConstraintLayout SecondStep;
    private Connection _context = null;

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


        ApplicationDbContext apdbc = new ApplicationDbContext();
        _context = apdbc.Connect();

        if (_context == null) {
            Toast.makeText(getApplicationContext(), "חיבור נכשל", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "חיבור נכשל", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "חיבור הסתיים בהצלחה", Toast.LENGTH_SHORT).show();
        }
    }

    public void NextStepRegister(View v) {
        FirstStep.setVisibility(View.INVISIBLE);
        SecondStep.setVisibility(View.VISIBLE);
    }

    public void PreviousStepRegister(View v) {
        FirstStep.setVisibility(View.VISIBLE);
        SecondStep.setVisibility(View.INVISIBLE);
    }

    public void AddUser(View v) {


        String firstName = Firstname.getText().toString();
        String lastname = Lastname.getText().toString();
        String email = Email.getText().toString();
        String phone = Phone.getText().toString();
        String username= Username.getText().toString();
        String password = Password.getText().toString();
        String repassword = RePassword.getText().toString();

    }
}