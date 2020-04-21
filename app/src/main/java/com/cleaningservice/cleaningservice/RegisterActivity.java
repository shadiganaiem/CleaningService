package com.cleaningservice.cleaningservice;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.text.Html;
import android.widget.Toast;
import android.widget.Switch;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.cleaningservice.cleaningservice.Services.MailService;
import com.cleaningservice.cleaningservice.Services.SMSService;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.SQLException;
import java.util.Random;

import Models.Customer;
import Models.User;

public class RegisterActivity extends AppCompatActivity  {

    //Database context
    private ApplicationDbContext _context = null;

    //Inputs validator class
    private Validator _validator = null;

    //Registration steps layouts
    private ConstraintLayout FirstStep;
    private ConstraintLayout SecondStep;

    //User input Details
    private TextInputEditText Firstname;
    private TextInputEditText Lastname;
    private TextInputEditText Email;
    private TextInputEditText Phone;
    private TextInputEditText Username;
    private TextInputEditText Password;
    private TextInputEditText RePassword;

    //Switch
    private Switch IsEmployee;

    //Services
    private PlacesClient placesClient;
    private SMSService _smsService;

    //Handler
    private Handler mainhandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        try {
            _context = ApplicationDbContext.getInstance(getApplicationContext());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        _smsService = new SMSService();
        _validator = new Validator();

        //layouts
        FirstStep = findViewById(R.id.FirstStep);
        SecondStep = findViewById(R.id.SecondStep);
        //User inputs
        Firstname = findViewById(R.id.Firstname);
        Lastname = findViewById(R.id.Lastname);
        Email = findViewById(R.id.Email);
        Phone= findViewById(R.id.Phone);
        Username = findViewById(R.id.Username);
        Password = findViewById(R.id.Password);
        RePassword = findViewById(R.id.RePassword);
        //user type
        IsEmployee = findViewById(R.id.Type);

    }

    /**
     * Validate all Inputs And get the next register step
     * @param v
     */
    public void NextStepRegister(View v) {

        String regex = "^[\\p{L} ]+$";
        boolean status = true;

        if(!_validator.InputValidate(Firstname,regex))
            status = false;
        if(!_validator.InputValidate(Lastname,regex))
            status = false;
        if(GetInputText(Phone).length() != 10){
            android.text.Spanned errorMsg  = Html.fromHtml("<font color='white'>מספר נייד לא תקין</font>");
            Phone.setError(errorMsg);
            status = false;
        }
        regex = "^[A-Za-z0-9_.-]+@(.+).[A-Za-z]+$";
        if(!_validator.InputValidate(Email,regex) || GetInputText(Email).equals("")){
            android.text.Spanned errorMsg  = Html.fromHtml("<font color='white'>דואר אלקטרוני אינו תקין</font>");
            Email.setError(errorMsg);
            status = false;
        }
        if(status){
            FirstStep.setVisibility(View.INVISIBLE);
            SecondStep.setVisibility(View.VISIBLE);
        }
    }

    /**
     * back to first register step
     * @param v
     */
    public void PreviousStepRegister(View v) {
        FirstStep.setVisibility(View.VISIBLE);
        SecondStep.setVisibility(View.INVISIBLE);
    }

    /**
     * add User to Database
     * @param v
     */
    public void AddUser(View v) {
        String regex = "^[\\p{L}0-9_]+$";
        boolean status = true;

        if(!_validator.InputValidate(Username,regex)){
            status = false;
        }
        if(status && _context.CheckIfUsernameExists(GetInputText(Username))) {
            android.text.Spanned errorMsg = Html.fromHtml("<font color='white'>שם משתמש קיים במערכת</font>");
            Username.setError(errorMsg);
            status = false;
        }
        if(!GetInputText(Password).equals(GetInputText(RePassword)) && !GetInputText(Password).equals("")){
            android.text.Spanned errorMsg = Html.fromHtml("<font color='white'>סיסמאות לא תואמות</font>");
            Password.setError(errorMsg);
            RePassword.setError(errorMsg);
            status = false;
        }
        if(_context.CheckIfEmailExists(GetInputText(Email))){
            android.text.Spanned errorMsg = Html.fromHtml("<font color='white'>דואר אלקטרוני נמצא במערכת</font>");
            Email.setError(errorMsg);
            Password.setError(errorMsg);
            RePassword.setError(errorMsg);
            status = false;
        }
        if(_context.CheckIfPhoneExists(GetInputText(Phone))){
            android.text.Spanned errorMsg = Html.fromHtml("<font color='white'>מספר נייד נמצא במערכת</font>");
            Phone.setError(errorMsg);
            Password.setError(errorMsg);
            RePassword.setError(errorMsg);
            status = false;
        }
        if (status){
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String table  = IsEmployee.isChecked() ?  "Employees" : "Customers";
                    String tableId = IsEmployee.isChecked() ? "EmployeeId" : "CustomerId";
                    String activationCode = GenerateActivationCode();
                    String query = "INSERT INTO " + table + "(Firstname,Lastname,Email,Phone) "+
                            "VALUES('"+ GetInputText(Firstname) +"','"+GetInputText(Lastname)+"','"+GetInputText(Email)+
                            "','"+GetInputText(Phone)+"');"+
                            "INSERT INTO Users(Username,"+ tableId +",Password,StatusId,ActivationCode,Rating) " + "SELECT '"+GetInputText(Username)+ "',MAX(ID),'"+GetInputText(Password)+"','"+Util.Statuses.DEACTIVATED.value+"','"
                            +activationCode+"','1' " +
                            "FROM "+ table +" WHERE "+table+".Phone = '"+GetInputText(Phone)+"';";
                    if(_context.ExecuteInsertData(query)) {
                        //SendConfirmationEmail(GetInputText(Email),activationCode);
                        User user = new User();
                        user.customer = new Customer();
                        user.customer.Phone = GetInputText(Phone);
                        user.ActivationCode = activationCode;
                       _smsService.SendActivationCode(getApplicationContext(), user);

                        try {
                            int id = _context.GetUserIdByUsername(GetInputText(Username));
                            _context.InitializeUserImage(id);

                            mainhandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(getBaseContext(), Activation.class);
                                    intent.putExtra("USER_ID", id);
                                    startActivity(intent);
                                }
                            });
                        } catch (Exception ex) {
                            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"אירעה שגיאה, נא לנסות מאוחר יותר", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            Thread thread = new Thread(runnable);
            thread.start();

        }
    }

    private String GenerateActivationCode(){
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    /**
     * get input text string
     * @param editText
     * @return InputValue
     */
    public String GetInputText(TextInputEditText editText){
        return editText.getText().toString();
    }

    public void SendConfirmationEmail(String userEmail,String activationCode ){
        try {
            String customerName = GetInputText(Firstname) + " " + GetInputText(Lastname);
            String body = "Dear "+customerName+",\n" +
                    "Thank you for choosing our system.\n\n"
                    +"To activate your account please enter the activation code below: \n"
                    +activationCode;

            String email = Util.GetProperty("mail.email",getApplicationContext());;
            String password = Util.GetProperty("mail.password",getApplicationContext());
            MailService _mailService = new MailService(email, password);
            _mailService.sendMail("CleaningService Activation Email",
                    body,
                    "CleaningService",
                    userEmail);
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }

    public void OpenMainActivity(View v){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

}