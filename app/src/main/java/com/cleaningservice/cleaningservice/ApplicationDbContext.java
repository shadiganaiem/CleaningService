package com.cleaningservice.cleaningservice;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.util.Base64;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Models.Customer;
import Models.Employee;
import Models.JobForm;
import Models.Status;
import Models.User;

public class ApplicationDbContext extends AppCompatActivity {

    private static ApplicationDbContext instance;
    private Connection _connection;

    private ApplicationDbContext(String driver,String url,String username,String password){
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

    /**
     * Get User By Id
     * @param id
     * @return
     */
    public User GetUser(int id){
        String query  = "SELECT * FROM Users WHERE ID="+id;
        try{
            ResultSet result = ExecuteSelectQuery(query);
            if (result.next()){
                User user = new User(
                        result.getString("Username"),
                        result.getString("Password"),
                        result.getString("ActivationCode"),
                        result.getInt("ID"),
                        result.getInt("CustomerId"),
                        result.getInt("EmployeeId"),
                        result.getInt("StatusId")
                );
                user.status = GetStatus(user.StatusId);
                user.employee = GetEmployee(user.EmployeeId);
                user.customer = GetCustomer(user.CustomerId);

                return user;
            }
        }
        catch (Exception ex){

        }
        return new User();
    }

    /**
     * Get All JobForms
     * @return
     */
    public List<JobForm> GetJobForms(){
        String query = "SELECT * FROM JobForms";
        List<JobForm> jobForms = new ArrayList<>();
        try{
            ResultSet result = ExecuteSelectQuery(query);
            while (result.next()){
                JobForm jobForm = new JobForm(
                        result.getInt("ID"),
                        result.getInt("CustomerId"),
                        result.getInt("Rooms"),
                        result.getString("City"),
                        result.getString("Address"),
                        result.getFloat("Budget"),
                        result.getDate("StartDate"),
                        result.getDate("EndDate"),
                        result.getInt("StatusId")
                );
                jobForm.customer = GetCustomer(jobForm.CustomerId);
                jobForm.status = GetStatus(jobForm.StatusId);

                jobForms.add(jobForm);

                query = "SELECT * FROM IMAGES WHERE JobFormId = "+jobForm.ID;
                ResultSet imageResultSet = ExecuteSelectQuery(query);
                if(imageResultSet.next()){
                    jobForm.bitmapString = imageResultSet.getString("Bitmap");
                }
            }

        }catch (Exception ex){

        }
        return jobForms;
    }

    public boolean InsertJobForm(JobForm jobForm,String StartDate,String EndDate){
        String query = "INSERT INTO JobForms(CustomerId,Rooms,City,Address,Budget,StartDate,EndDate,StatusId)";
        query += "VALUES("+jobForm.CustomerId + ","+jobForm.Rooms + ",'"+jobForm.City+"','"+jobForm.Address + "',"+
                jobForm.Budget + ",'"+StartDate + "','"+EndDate+"',3)";

        return ExecuteInsertData(query);
    }
    /**
     * Get User Object By username
     * @param username
     * @return
     */
    public User GetUser(String username){
        String query  = "SELECT * FROM Users WHERE Username='"+username+"'";
        try{
            ResultSet result = ExecuteSelectQuery(query);
            if (result.next()){
                User user = new User(
                        result.getString("Username"),
                        result.getString("Password"),
                        result.getString("ActivationCode"),
                        result.getInt("ID"),
                        result.getInt("CustomerId"),
                        result.getInt("EmployeeId"),
                        result.getInt("StatusId")
                );

                return user;
            }
        }
        catch (Exception ex){

        }
        return new User();
    }

    /**
     * Get Customer By Id
     * @param id
     * @return
     */
    public Customer GetCustomer(int id){
        String query  = "SELECT * FROM Customers WHERE ID="+id;
        try{
            ResultSet result = ExecuteSelectQuery(query);
            if (result.next()){
                Customer customer = new Customer(
                        result.getInt("ID"),
                        result.getString("Firstname"),
                        result.getString("Lastname"),
                        result.getString("Email"),
                        result.getString("Phone")
                );

                query = "SELECT * FROM USERS WHERE CustomerId="+id;
                result = ExecuteSelectQuery(query);
                if(result.next())
                    customer.Rating = result.getInt("Rating");
                return customer;
            }
        }
        catch (Exception ex){

        }
        return new Customer();
    }

    /**
     * Get Employee By Id
     * @param id
     * @return
     */
    public Employee GetEmployee(int id){
        String query = "SELECT * FROM Employees WHERE ID="+id;
        try{
            ResultSet result = ExecuteSelectQuery(query);
            if (result.next()){
                Employee employee = new Employee(
                        result.getInt("ID"),
                        result.getString("Firstname"),
                        result.getString("Lastname"),
                        result.getString("Email"),
                        result.getString("Phone")
                );
                return employee;
            }
        }
        catch (Exception ex){

        }
        return new Employee();
    }

    /**
     * Get Status By Id
     * @param id
     * @return
     */
    public Status GetStatus(int id){
        String query = "SELECT * FROM Statuses WHERE ID="+id;
        try{
            ResultSet result = ExecuteSelectQuery(query);
            if (result.next()){
                Status status = new Status(
                        result.getInt("ID"),
                        result.getString("Name")
                );
                return status;
            }
        }
        catch (Exception ex){

        }
        return new Status();
    }

    /**
     * Get Connection
     * @return
     */
    public Connection getConnection(){
        return _connection;
    }


    public boolean InsertImage(int jobFormId , Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String bitmapString= Base64.encodeToString(b, Base64.DEFAULT);

        String query = "INSERT INTO Images(JobFormId,Bitmap) Values('"+ jobFormId + "','"+bitmapString+"')";
        return ExecuteInsertData(query);
    }

    /**
     * Get ApplicationDbContext instance ( Singleton )
     * @param context1
     * @return
     * @throws SQLException
     */
    public static ApplicationDbContext getInstance(Context context1) throws SQLException {
        if (instance == null) {
            try {
                instance = new ApplicationDbContext(
                        Util.DBProperty("db.driver", context1),
                        Util.DBProperty("db.url", context1),
                        Util.DBProperty("db.username", context1),
                        Util.DBProperty("db.password", context1));
            } catch (Exception ex) {
                Toast.makeText(context1, "אין חיבור", Toast.LENGTH_SHORT).show();
            }
        }
        else if (instance.getConnection().isClosed()){
            try {
                instance = new ApplicationDbContext(
                        Util.DBProperty("db.driver", context1),
                        Util.DBProperty("db.url", context1),
                        Util.DBProperty("db.username", context1),
                        Util.DBProperty("db.password", context1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
}
