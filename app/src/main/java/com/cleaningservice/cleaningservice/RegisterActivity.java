package com.cleaningservice.cleaningservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;

public class RegisterActivity extends AppCompatActivity {
    private ConstraintLayout FirstStep;
    private ConstraintLayout SecondStep;
    private Connection _context = null;

    private EditText FirstName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirstStep = findViewById(R.id.FirstStep);
        SecondStep = findViewById(R.id.SecondStep);
        FirstName = findViewById(R.id.Firstname);

        ApplicationDbContext apdbc = new ApplicationDbContext();
        _context = apdbc.Connect();

        if (_context == null) {
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
        String firstName = FirstName.getText().toString();
    }
}