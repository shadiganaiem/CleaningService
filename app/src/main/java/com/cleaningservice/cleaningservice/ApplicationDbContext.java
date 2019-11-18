package com.cleaningservice.cleaningservice;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.xml.validation.Validator;

public class ApplicationDbContext {
    private Connection _connection;
    private String query;

    public ApplicationDbContext(){
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
        _connection = connection;
    }


    public void ExecuteQuery(String query){
            this.query  = query;
            InsertData insert = new InsertData();
            insert.execute();
    }

    private class InsertData extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... strings){
            try {
                Statement stmt = _connection.createStatement();
                ResultSet result = stmt.executeQuery(query);
                _connection.close();

            }catch (Exception ex){
                ex.printStackTrace();
            }

            return "";
        }
    }
}
