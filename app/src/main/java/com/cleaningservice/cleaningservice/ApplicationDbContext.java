package com.cleaningservice.cleaningservice;

import android.os.AsyncTask;
import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;

public class ApplicationDbContext {

    public Connection Connect(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionURL = null;
        try{
           Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
           connectionURL = "jdbc:jtds:sqlserver://82.80.211.84;database=CleaningService;user=CleaningService_User;password=CleaningService_User";

           connection = DriverManager.getConnection(connectionURL);
        }catch (Exception ex){
            String e = ex.getMessage();
            System.out.println(e);
        }
        return connection;
    }
}
