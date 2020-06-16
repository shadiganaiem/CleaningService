package com.cleaningservice.cleaningservice;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cleaningservice.cleaningservice.Services.SMSService;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText Username;
    private TextInputEditText Password;
    private Validator _validator = null;
    private Button login;
    private SMSService _smsService = null;
    private ApplicationDbContext _context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_login);


        findViewById(R.id.show_languages).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageDialog();
            }
        });

        Username = findViewById(R.id.LoginUsername);
        Password = findViewById(R.id.LoginPassword);
//
        try {
            _context = ApplicationDbContext.getInstance(getApplicationContext());
            _smsService = new SMSService();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        _validator = new Validator();
        Intent intent = getIntent();
        if(intent.getBooleanExtra("flag", false)){
            Toast.makeText(getApplicationContext(),this.getResources().getString(R.string.accountCreated), Toast.LENGTH_SHORT).show();
        }
        login =findViewById(R.id.button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.progressBar0).setVisibility(View.VISIBLE);
                findViewById(R.id.loginlayout).setVisibility(View.INVISIBLE);
                Login();
                findViewById(R.id.progressBar0).setVisibility(View.INVISIBLE);
                findViewById(R.id.loginlayout).setVisibility(View.VISIBLE);
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

    public void Login(){

        String regex = "^[\\p{L}0-9_]+$";
        boolean status = true;

        if(!_validator.InputValidate(Username,regex)){
            status = false;
        }

        if(GetInputText(Password).contains("'") || GetInputText(Password).contains("'OR") || GetInputText(Password).contains("'or") || GetInputText(Password).contains("' or")
            ||GetInputText(Password).contains("' OR"))
            return;

        if (status){
            String query = "SELECT ID ,StatusId , CustomerId , EmployeeId FROM Users WHERE Username='"+GetInputText(Username)+
                    "' and Password='"+GetInputText(Password)+"'";
           ResultSet result =  _context.ExecuteSelectQuery(query);
           try{

               if(result.next()){
                   String activationCode = GenerateActivationCode();
                   int customerId = result.getInt("CustomerId");
                   int employeeId = result.getInt("EmployeeId");
                   if(result.getInt("StatusId") == Util.Statuses.ACTIVATED.value || result.getInt("StatusId") == Util.Statuses.DEACTIVATED.value){
                       String Phone;
                       if(customerId != 0)
                           Phone = _context.GetCustomer(customerId).Phone;
                       else
                           Phone = _context.GetEmployee(employeeId).Phone;

                      // query = "UPDATE Users SET ActivationCode = '" + activationCode + "' WHERE ID=" + result.getInt("ID");
                     //  if (_context.ExecuteInsertData(query)) {
                       //    _smsService.SendLoginActication(getApplicationContext(),Phone,activationCode);
                         //  Toast.makeText(getApplicationContext(), "קוד כניסה נשלח אליך", Toast.LENGTH_SHORT).show();
                       //}else {
                         //  Toast.makeText(getApplicationContext(), "קוד לא נשלח!", Toast.LENGTH_SHORT).show();
                       //}

                       Intent intent = new Intent(getBaseContext(), Activation.class);
                       intent.putExtra("USER_ID", result.getInt("ID"));
                       startActivity(intent);
                   }
                   else{
                       Username.setError(this.getResources().getString(R.string.accountBlocked));
                       Password.setError(this.getResources().getString(R.string.accountBlocked));
                   }
               }
               else{
                   Username.setError(this.getResources().getString(R.string.LoginFailed));
                   Password.setError(this.getResources().getString(R.string.LoginFailed));
               }
           }
           catch (Exception ex){
               ex.printStackTrace();
               Toast.makeText(getApplicationContext(),ex.toString(), Toast.LENGTH_SHORT).show();
           }

        }

    }

    /**
     * @param editText
     * @return InputValue
     */
    public String GetInputText(TextInputEditText editText){
        return editText.getText().toString();
    }

    public void OpenRegisterActivity(View v){
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    public void OpenMainActivity(View v){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    private String GenerateActivationCode(){
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format(Locale.US,"%06d", number);
    }

    public void ResetPassword (View v){
        Intent intent = new Intent(getBaseContext(), ResetPassword.class);
        startActivity(intent);
    }
}
