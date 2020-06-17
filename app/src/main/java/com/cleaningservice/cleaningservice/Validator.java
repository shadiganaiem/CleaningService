package com.cleaningservice.cleaningservice;

import android.content.Context;
import android.text.Html;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Pattern;

public class Validator {

    /**
     * Validate text and get a relevant error message
     * @param editText : input EditText to validate
     */
    public boolean InputValidate(Context context,TextInputEditText editText, String regularExpression){

        android.text.Spanned errorMsg = Html.fromHtml("<font color='white'>"+context.getResources().getString(R.string.required)+"</font>");
        Pattern pattern = Pattern.compile(regularExpression);

        String text =GetInputText(editText);

        //Validate Text and get a relevant Error message
        if(text.equals("")){
            editText.setError(errorMsg);
            return false;
        }
        else if(!pattern.matcher(text).matches()){
            errorMsg = Html.fromHtml("<font color='white'>"+context.getResources().getString(R.string.noSymbols)+"</font>");
            editText.setError(errorMsg);
            return false;
        }
        return true;
    }

    /**
     * @param editText
     * @return InputValue
     */
    public String GetInputText(TextInputEditText editText){
        return editText.getText().toString();
    }

}
