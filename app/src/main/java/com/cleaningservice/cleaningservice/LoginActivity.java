package com.cleaningservice.cleaningservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import java.sql.ResultSet;
import java.sql.SQLException;

import Authentications.Preferences;
import Models.User;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText Username;
    private TextInputEditText Password;


    private ApplicationDbContext _context = null;
    private Validator _validator = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Username = findViewById(R.id.LoginUsername);
        Password = findViewById(R.id.LoginPassword);

       /* try{
            _context = new ApplicationDbContext(
                    Util.DBProperty("db.driver",getApplicationContext()),
                    Util.DBProperty("db.url",getApplicationContext()),
                    Util.DBProperty("db.username",getApplicationContext()),
                    Util.DBProperty("db.password",getApplicationContext()));
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(),"אין חיבור", Toast.LENGTH_SHORT).show();
        }*/

        try {
            _context = ApplicationDbContext.getInstance(getApplicationContext());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        _validator = new Validator();
        Intent intent = getIntent();
        if(intent.getBooleanExtra("flag", false)){
            Toast.makeText(getApplicationContext(),"חשבון נוצר בהצלחה", Toast.LENGTH_SHORT).show();
        }
    }

    public void Login(View v){
        String regex = "^[\\p{L}0-9_]+$";
        boolean status = true;

        if(!_validator.InputValidate(Username,regex)){
            status = false;
        }

        if (status){
            String query = "SELECT * FROM Users WHERE Username='"+GetInputText(Username)+
                    "' and Password='"+GetInputText(Password)+"'";
           ResultSet result =  _context.ExecuteSelectQuery(query);

           try{
               if(result.next()){
                   User user = _context.GetUser((result.getInt("ID")));

                   if(user.StatusId == 2){
                       Intent intent = new Intent(this,HomeActivity.class);
                       Preferences sp = new Preferences();
                       sp.SetUserID(this,user.ID,user.CustomerId!=0?true:false);
                       startActivity(intent);
                   }
                   else{
                       Intent intent = new Intent(getBaseContext(), Activation.class);
                       intent.putExtra("USER_ID", user.ID);
                       startActivity(intent);
                   }
               }
               else{
                   Username.setError("שם משתמש וסיסמה לא תואמות");
                   Password.setError("שם משתמש וסיסמה לא תואמות");
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
}
