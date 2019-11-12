package com.cleaningservice.cleaningservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class RegisterActivity extends AppCompatActivity {
    private ConstraintLayout FirstStep;
    private ConstraintLayout SecondStep;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirstStep = findViewById(R.id.FirstStep);
        SecondStep = findViewById(R.id.SecondStep);

        FirstStep.setVisibility(View.VISIBLE);
        SecondStep.setVisibility(View.INVISIBLE);
    }

    public void NextStepRegister(View v){
        FirstStep.setVisibility(View.INVISIBLE);
        SecondStep.setVisibility(View.VISIBLE);
    }
}
