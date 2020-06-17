package com.cleaningservice.cleaningservice;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import Authentications.Preferences;

public class MainActivity extends AppCompatActivity {
    private TextView SignUpButton;
    private Button SignInButton;
    private Preferences sp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        sp = new Preferences();
        int userId = sp.GetLoggedInUserID(this);
        if(userId != 0){
            Intent intent = new Intent(this,HomeActivity.class);
            startActivity(intent);
        }

        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);

          SignUpButton= findViewById(R.id.SignUpBtn);
          SignInButton= findViewById(R.id.SignInBtn);

        findViewById(R.id.show_languages).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageDialog();
            }
        });

    }



    public void showLanguageDialog(){
        final String[] languagesList = { "العربية", "עברית" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.chooseLanguage));
        builder.setSingleChoiceItems(languagesList, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0) {
                    setLocale("ar");
                    recreate();
                }
                else if (which ==1){
                    setLocale("he");
                    recreate();
                }
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void setLocale(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = prefs.getString("My_Lang","");
        setLocale(lang);
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
