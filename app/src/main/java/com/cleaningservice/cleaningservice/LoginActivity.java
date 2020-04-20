package com.cleaningservice.cleaningservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cleaningservice.cleaningservice.Services.SMSService;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Random;

import Authentications.Preferences;
import Models.Employee;
import Models.User;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText Username;
    private TextInputEditText Password;
    private Validator _validator = null;
    private SMSService _smsService = null;
    private ApplicationDbContext _context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Username = findViewById(R.id.LoginUsername);
        Password = findViewById(R.id.LoginPassword);

        try {
            _context = ApplicationDbContext.getInstance(getApplicationContext());
            _smsService = new SMSService();
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

                       query = "UPDATE Users SET ActivationCode = '" + activationCode + "' WHERE ID=" + result.getInt("ID");
                       if (_context.ExecuteInsertData(query)) {
                           _smsService.SendLoginActication(getApplicationContext(),Phone,activationCode);
                           Toast.makeText(getApplicationContext(), "קוד כניסה נשלח אליך", Toast.LENGTH_SHORT).show();
                       }else {
                           Toast.makeText(getApplicationContext(), "קוד לא נשלח!", Toast.LENGTH_SHORT).show();
                       }

                       Intent intent = new Intent(getBaseContext(), Activation.class);
                       intent.putExtra("USER_ID", result.getInt("ID"));
                       startActivity(intent);
                   }
                   else{
                       Username.setError("חשבון חסום");
                       Password.setError("חשבון חסום");
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
