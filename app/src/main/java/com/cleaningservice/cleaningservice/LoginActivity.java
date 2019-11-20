package com.cleaningservice.cleaningservice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText Usermame;
    private TextInputEditText Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Usermame = findViewById(R.id.LoginUsername);
        Password = findViewById(R.id.LoginPassword);
    }

    public void Login(View v){


    }
}
