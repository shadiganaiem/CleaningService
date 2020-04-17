package com.cleaningservice.cleaningservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cleaningservice.cleaningservice.Services.MailService;
import com.cleaningservice.cleaningservice.Services.SMSService;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Random;

import Authentications.Preferences;
import Models.Customer;
import Models.Employee;
import Models.User;

public class Activation extends AppCompatActivity {

    private TextInputEditText codeInput;

    //Database context
    private ApplicationDbContext _context = null;
    //SMS SERVICE
    private SMSService _smsService = null;
    private User user;

    //Current User

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);

        try {
            _context = ApplicationDbContext.getInstance(getApplicationContext());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        _smsService = new SMSService();
        int userId = getIntent().getIntExtra("USER_ID", 0);
        user = _context.UserDetails(userId);
        if(user.EmployeeId != 0){
            user.employee = _context.GetEmployee(user.EmployeeId);
        }
        else{
            user.customer = _context.GetCustomer(user.CustomerId);
        }

        codeInput = findViewById(R.id.code);
        codeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                ProgressBar loader = findViewById(R.id.progressBar);
                loader.setVisibility(View.VISIBLE);
                if (s.length() == 6) {
                    try {

                        if (s.toString().equals(user.ActivationCode)) {
                            if (ActivateAccount()) {
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                Preferences sp = new Preferences();
                                sp.SetUserID(getApplicationContext(), user.ID, user.CustomerId != 0 ? true : false);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "שגיאה באימות", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "קוד אימות לא תואם", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                loader.setVisibility(View.INVISIBLE);
            }
        });
    }

    private boolean ActivateAccount(){
        try{
            String query = "UPDATE USERS SET StatusId = "+Util.Statuses.ACTIVATED.value+" WHERE ID = "+user.ID+";";

            if(user.StatusId == Util.Statuses.ACTIVATED.value)
                return true;
            if(_context.ExecuteInsertData(query)){
                SendActivationEmail();
                return true;
            }
            else{
                Toast.makeText(getApplicationContext(),"אירעה שגיאה, נא לנסות מאוחר יותר", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception ex){
            Toast.makeText(getApplicationContext(),ex.toString(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void SendActivationEmail(){
        try {
            String userEmail ="";
            if(user.EmployeeId != 0){
                userEmail = user.employee.Email;
            }
            else if(user.customer !=  null){
                userEmail = user.customer.Email;
            }

            String body = "Dear Customer \n" +
                    "Your Account has been activated successfully\n\n"
                    +"Thank You for being a part of our family.\n";

            MailService _mailService = new MailService("cleaningservice.project.sce@gmail.com", "Project@00");
            _mailService.sendMail("CleaningService Activation Email",
                    body,
                    "CleaningService",
                    userEmail);
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
            Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void ReSendActicationCode(View v) {
        String activationCode = GenerateActivationCode();
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView textView = findViewById(R.id.sendCode);

        progressBar.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);


        String query = "UPDATE Users SET ActivationCode = '" + activationCode + "' WHERE ID=" + user.ID + ";";
        if (_context.ExecuteInsertData(query)) {
            user.ActivationCode = activationCode;

            _smsService.SendActivationCode(getApplicationContext(),user);

            Toast.makeText(getApplicationContext(), "קוד חדש נשלח אליך בהצלחה", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }else {
            Toast.makeText(getApplicationContext(), "קוד לא נשלח!", Toast.LENGTH_SHORT).show();
            textView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private String GenerateActivationCode(){
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format(Locale.US,"%06d", number);
    }

    public void SendMail() throws Exception {
        String customerName = "";
        String userEmail= "";
        if(user.EmployeeId != 0){
            customerName = user.employee.Firstname + " " +user.employee.Lastname;
            userEmail = user.employee.Email;
        }
        else if(user.CustomerId != 0){
            customerName = user.customer.Firstname + " " +user.customer.Lastname;
            userEmail = user.customer.Email;
        }

        String body = "Dear " + customerName + ",\n" +
                "Thank you for choosing our system.\n\n"
                + "To activate your account please enter the activation code below: \n"
                + user.ActivationCode;
        String email = Util.GetProperty("mail.email", getApplicationContext());
        String password = Util.GetProperty("mail.password", getApplicationContext());
        MailService _mailService = new MailService(email, password);
        _mailService.sendMail("CleaningService Activation Email",
                body,
                "CleaningService",
                userEmail);

    }

}
