package com.cleaningservice.cleaningservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cleaningservice.cleaningservice.Services.SMSService;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Random;

import Authentications.Preferences;

public class ResetPassword extends AppCompatActivity {

    private SMSService _smsService = null;
    private ApplicationDbContext _context = null;

    private Validator _validator = null;
    private TextInputEditText password;
    private TextInputEditText rePassword;
    private TextInputEditText phone;
    private TextInputEditText resetCode;
    private Button sendCodeBtn;
    private Button ResetLoginButton;
    private String ActivationCode;
    private Handler mainhandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        rePassword = findViewById(R.id.reNewPassword);
        password = findViewById(R.id.newPassword);

        try {
            _context = ApplicationDbContext.getInstance(getApplicationContext());
            _smsService = new SMSService();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        _validator = new Validator();

        phone = findViewById(R.id.Phone);
        password = findViewById(R.id.newPassword);
        resetCode = findViewById(R.id.resetCode);
        sendCodeBtn = findViewById(R.id.sendCodeBtn);
        rePassword = findViewById(R.id.reNewPassword);
        ResetLoginButton = findViewById(R.id.ResetLoginButton);

        resetCode.addTextChangedListener(new TextWatcher() {
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
                if (s.length() == 6) {
                    try {
                        if (s.toString().equals(ActivationCode)) {
                            phone.setVisibility(View.INVISIBLE);
                            resetCode.setVisibility(View.INVISIBLE);

                            password.setVisibility(View.VISIBLE);
                            rePassword.setVisibility(View.VISIBLE);
                            ResetLoginButton.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.ActivationCodeNotValid), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void SendActivationCode(View v) {

        String test = GetInputText(phone);
        if (GetInputText(phone).length() != 10) {
            android.text.Spanned errorMsg = Html.fromHtml("<font color='white'>"+getResources().getString(R.string.PhoneNumberNotValid)+"</font>");
            phone.setError(errorMsg);
            return;
        }

        if (!_context.CheckIfPhoneExists(GetInputText(phone))) {
            android.text.Spanned errorMsg = Html.fromHtml("<font color='white'>"+getResources().getString(R.string.phoneNumberNotExist)+"</font>");
            phone.setError(errorMsg);
            return;
        }

        phone.setEnabled(false);
        resetCode.setVisibility(View.VISIBLE);
        sendCodeBtn.setVisibility(View.INVISIBLE);

        ActivationCode = GenerateActivationCode();
        if (_context.UpdateActivationCodeByPhone(GetInputText(phone), ActivationCode)) {
            if (!_smsService.SendResetCode(getApplicationContext(), GetInputText(phone), ActivationCode)) {
                Toast.makeText(getApplicationContext(), this.getResources().getString(R.string.newCodeSent), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), this.getResources().getString(R.string.exception), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.exception), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * get input text string
     *
     * @param editText
     * @return InputValue
     */
    public String GetInputText(TextInputEditText editText) {
        return editText.getText().toString();
    }

    public void ChangePassword(View v) {
        if (!GetInputText(password).equals(GetInputText(rePassword)) && !GetInputText(password).equals("") && !GetInputText(password).equals("")) {
            android.text.Spanned errorMsg = Html.fromHtml("<font color='white'>"+this.getResources().getString(R.string.passwordnotmatch)+"</font>");
            password.setError(errorMsg);
            rePassword.setError(errorMsg);
            return;
        }
        else if(GetInputText(password).length() < 8){
            android.text.Spanned errorMsg = Html.fromHtml("<font color='white'>"+this.getResources().getString(R.string.passwordValidate)+"</font>");
            password.setError(errorMsg);
            rePassword.setError(errorMsg);
            return;
        }

        if (_context.ChangePasswordByPhone(GetInputText(phone), GetInputText(password))) {
            Toast.makeText(getApplicationContext(), this.getResources().getString(R.string.passwordUpdated), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(getApplicationContext(), this.getResources().getString(R.string.exception), Toast.LENGTH_SHORT).show();
        }
    }


    public void BackBtn(View v){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    private String GenerateActivationCode() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format(Locale.US, "%06d", number);
    }
}
