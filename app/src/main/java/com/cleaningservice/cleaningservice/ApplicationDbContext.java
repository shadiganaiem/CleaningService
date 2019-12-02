package com.cleaningservice.cleaningservice;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

import javax.xml.validation.Validator;

public class ApplicationDbContext extends AppCompatActivity {
    private Connection _connection;
    private String query;

    public ApplicationDbContext(String driver,String url,String username,String password){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionURL = null;
        try{
            //Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            //connectionURL = "jdbc:jtds:sqlserver://82.80.211.84/CleaningService;user=CleaningService_User;password=CleaningService_User";

            Class.forName(driver).newInstance();
            connectionURL = url+";user="+username+";password="+password+";";
            connection = DriverManager.getConnection(connectionURL);
        }catch (Exception ex){
            String e = ex.getMessage();
            System.out.println(e);
        }
        _connection = connection;

    }

    /**
     * Execute an executable Query.
     * @param query
     */
    public boolean ExecuteInsertData(String query){

        try {
            Statement stmt = _connection.createStatement();
            stmt.executeUpdate(query);
            _connection.close();
            return true;

        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Execute an executable Query.
     * @param query
     */
    public ResultSet ExecuteSelectQuery(String query){

        try {
            Statement stmt = _connection.createStatement();
            ResultSet result = stmt.executeQuery(query);
            return result;

        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}
