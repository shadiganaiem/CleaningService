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

import com.google.android.material.textfield.TextInputEditText;

import java.util.Random;

import Models.UserViewModel;

public class Activation extends AppCompatActivity {

    private TextInputEditText codeInput;
    private UserViewModel userViewModel;

    //Database context
    private ApplicationDbContext _context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);

        try{
            _context = new ApplicationDbContext(
                    Util.DBProperty("db.driver",getApplicationContext()),
                    Util.DBProperty("db.url",getApplicationContext()),
                    Util.DBProperty("db.username",getApplicationContext()),
                    Util.DBProperty("db.password",getApplicationContext()));
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(),"אין חיבור", Toast.LENGTH_SHORT).show();
        }

        int userId = getIntent().getIntExtra("USER_ID",0);
        userViewModel = new UserViewModel();
        userViewModel.User = _context.GetUser(userId);

        try{
            userViewModel.User.next();
            int id = userViewModel.User.getInt("CustomerId");
            if(id != 0){
                userViewModel.Customer = _context.GetCustomer(id);
                userViewModel.Customer.next();
            }
            id = userViewModel.User.getInt("EmployeeId");
            if(id != 0){
                userViewModel.Employee = _context.GetEmployee(id);
                userViewModel.Employee.next();
            }
        }
        catch (Exception ex){
            Toast.makeText(getApplicationContext(),ex.toString(), Toast.LENGTH_SHORT).show();
        }


        codeInput = findViewById(R.id.code);
        codeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                ProgressBar loader = findViewById(R.id.progressBar);
                loader.setVisibility(View.VISIBLE);
                if(s.length() == 6) {
                    try {

                        if (s.toString().equals(userViewModel.User.getString("ActivationCode"))){
                            if(ActivateAccount()) {
                                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"שגיאה באימות", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"קוד אימות לא תואם", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception ex) {
                        Toast.makeText(getApplicationContext(),ex.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                loader.setVisibility(View.INVISIBLE);
            }
        });
    }

    private boolean ActivateAccount(){
        try{
            String query = "UPDATE USERS SET STATUS = 1 WHERE ID = "+userViewModel.User.getInt("ID")+";";

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
            if(userViewModel.Employee != null){
                userEmail = userViewModel.Employee.getString("Email");
            }
            else if(userViewModel.Customer != null){
                userEmail = userViewModel.Customer.getString(("Email"));
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

    public void ReSendActicationCode(View v){
        String activationCode = GenerateActivationCode();
        try {
            ProgressBar progressBar = findViewById(R.id.progressBar);
            TextView textView = findViewById(R.id.sendCode);

            progressBar.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);

            String customerName = "";
            String userEmail= "";
            if(userViewModel.Employee != null){
                customerName = userViewModel.Employee.getString("Firstname") + " " +userViewModel.Employee.getString("Lastname");
                userEmail = userViewModel.Employee.getString("Email");
            }
            else if(userViewModel.Customer != null){
                customerName = userViewModel.Customer.getString("Firstname") + " " +userViewModel.Customer.getString("Lastname");
                userEmail = userViewModel.Customer.getString("Email");
            }

            String query = "UPDATE Users SET ActivationCode = '"+activationCode+"' WHERE ID="+userViewModel.User.getInt("ID")+";";
            if(_context.ExecuteInsertData(query)){
                String body = "Dear "+customerName+",\n" +
                        "Thank you for choosing our system.\n\n"
                        +"To activate your account please enter the activation code below: \n"
                        +activationCode;
                MailService _mailService = new MailService("cleaningservice.project.sce@gmail.com", "Project@00");
                _mailService.sendMail("CleaningService Activation Email",
                    body,
                    "CleaningService",
                    userEmail);

                userViewModel.User = _context.GetUser(userViewModel.User.getInt("ID"));
                userViewModel.User.next();
                Toast.makeText(getApplicationContext(),"קוד חדש נשלח לך בהצלחה", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
            else{
                Toast.makeText(getApplicationContext(),"המייל לא נשלח!", Toast.LENGTH_SHORT).show();
                textView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }

    private String GenerateActivationCode(){
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

}
