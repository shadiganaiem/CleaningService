package com.cleaningservice.cleaningservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button SignUpButton;
    private Button SignInButton;
    private Button HomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SignUpButton= findViewById(R.id.SignUpBtn);
        SignInButton= findViewById(R.id.SignInBtn);
        HomeButton= findViewById(R.id.HomeBtn);
        HomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenHomeActivity();
            }
        });

    }

    public void OpenHomeActivity(){
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
    }

    public void OpenRegisterActivity(View v){
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    public void OpenLoginActivity(View v){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }
}
