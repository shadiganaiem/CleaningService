package com.cleaningservice.cleaningservice.Services;

import android.content.Context;

import com.cleaningservice.cleaningservice.R;
import com.cleaningservice.cleaningservice.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import Models.User;

public class SMSService {

    public boolean SendActivationCode(Context context, User user) {
        try {
            String username = Util.GetProperty("sms.username", context);
            String password = Util.GetProperty("sms.password", context);

            String destinationPhone = user.customer != null ? user.customer.Phone : user.employee.Phone;

            String message = context.getResources().getString(R.string.ActivationFirstLine) + "\n" +
                    context.getResources().getString(R.string.ActivationSecondLine) + "\n" +
                    context.getResources().getString(R.string.ActivationThirdLine) + "\n" +
                    user.ActivationCode;

            String xmlBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<sms>" +
                    "<user>" +
                    "<username>" + username + "</username>" +
                    "<password>" + password + "</password>" +
                    "</user>" +
                    "<source>CleaningSRV</source>" +
                    "<destinations>" +
                    "<phone>"+destinationPhone+"</phone>" +
                    "</destinations>" +
                    "<message>" + message + "</message>" +
                    "<response>0</response>" +
                    "</sms>";

            return this.SendSMS(xmlBody);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean SendLoginActication(Context context,String destinationPhone,String activationCode) {
        try {
            String username = Util.GetProperty("sms.username", context);
            String password = Util.GetProperty("sms.password", context);

            String xmlBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<sms>" +
                    "<user>" +
                    "<username>" + username + "</username>" +
                    "<password>" + password + "</password>" +
                    "</user>" +
                    "<source>CleaningSRV</source>" +
                    "<destinations>" +
                    "<phone>"+destinationPhone+"</phone>" +
                    "</destinations>" +
                    "<message>Your Verification Code is:" + activationCode + "</message>" +
                    "<response>0</response>" +
                    "</sms>";

            return this.SendSMS(xmlBody);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public  boolean SendSMS(String xmlBody) throws IOException {

        URL url = new URL("https://www.019sms.co.il:8090/api/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setUseCaches(true);
        connection.setRequestMethod("POST");

        // Set Headers
        connection.setRequestProperty("Accept", "application/xml");
        connection.setRequestProperty("Content-Type", "application/xml");

        // Write XML
        OutputStream outputStream = connection.getOutputStream();
        byte[] b = xmlBody.getBytes("UTF-8");
        outputStream.write(b);
        outputStream.flush();
        outputStream.close();

        // Read XML
        InputStream inputStream = connection.getInputStream();
        byte[] res = new byte[2048];
        int i = 0;
        StringBuilder response = new StringBuilder();
        while ((i = inputStream.read(res)) != -1) {
            response.append(new String(res, 0, i));
        }
        inputStream.close();

        System.out.println("Response= " + response.toString());

        return GetResponse(res.toString());
    }

    /// <summary>
    /// Get Request Result For Sending SMS's
    /// </summary>
    /// <param name="result"></param>
    /// <returns></returns>
    private boolean GetResponse(String result)
    {
        String res = "CallToSupport";

        if (result == null)
        {
            res = "UnknownError";
            return false;
        }

        if (result.contains("<status>0</status>"))
        {
            res = "Success";
            return true;
        }

        if (result.contains("<status>1</status>"))
        {
            res = "XmlError";
            return false;
        }

        if (result.contains("<status>2</status>"))
        {
            res = "MissingField";
            return false;
        }

        if (result.contains("<status>3</status>"))
        {
            res = "BadLogin";
            return false;
        }

        if (result.contains("<status>4</status>"))
        {
            res = "NoCredits";
            return false;
        }

        if (result.contains("<status>5</status>"))
        {
            res = "NoPermission";
            return false;
        }

        if (result.contains("<status>997</status>"))
        {
            res = "InvalidCommand";
            return false;
        }

        if (result.contains("<status>998</status>") || !result.contains("<status>999</status>"))
        {
            res = "UnknownError";
            return false;
        }

        return res.toLowerCase().equals("success");
    }

}
