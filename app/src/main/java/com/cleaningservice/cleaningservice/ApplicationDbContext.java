package com.cleaningservice.cleaningservice;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.util.Base64;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.sourceforge.jtds.jdbc.DateTime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    public List<JobForm> GetJobForms(int minRate,int maxRate){
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String today = dateFormat.format(date);

        String query = "SELECT * FROM JobForms JOIN Users ON JobForms.CustomerId = Users.CustomerId"+
                " WHERE JobForms.EndDate >= '"+today+"' AND Users.Rating >= "+minRate + " AND Users.Rating <= "+maxRate+
                " AND JobForms.StatusId = 3";
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

                query = "SELECT TOP 1 ImageBytes FROM IMAGES WHERE JobFormId = "+jobForm.ID;
                ResultSet imageResultSet = ExecuteSelectQuery(query);
                if(imageResultSet.next()){
                    jobForm.ImageBytes  = imageResultSet.getBytes("ImageBytes");
                }
            }

        }catch (Exception ex){

        }
        return jobForms;
    }

    /**
     * get all job forms for this week.
     * @return
     */
    public List<JobForm> GetJobFormsForThisWeek(int minRate, int maxRate){
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String today = dateFormat.format(date);
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        c.add(Calendar.DATE, 7-dayOfWeek);
        date = c.getTime();
        String lastDateInThisWeek = dateFormat.format(date);
        String query = "SELECT * FROM JobForms JOIN USERS ON JobForms.CustomerId = Users.CustomerId" +
                " WHERE JobForms.EndDate >= '"+today+"' AND JobForms.EndDate<= '" +lastDateInThisWeek + "'"+
                " AND Users.Rating >= "+minRate + " AND Users.Rating <= "+maxRate+
                " AND JobForms.StatusId = 3";
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

                query = "SELECT TOP 1 ImageBytes FROM IMAGES WHERE JobFormId = "+jobForm.ID;
                ResultSet imageResultSet = ExecuteSelectQuery(query);
                if(imageResultSet.next()){
                    jobForm.ImageBytes  = imageResultSet.getBytes("ImageBytes");
                }
            }

        }catch (Exception ex){

        }
        return jobForms;
    }

    /**
     * Get all jobforms for this month.
     * @return
     */
    public List<JobForm> GetJobFormsForThisMonth(int minRate,int maxRate){
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String today = dateFormat.format(date);
        date = new Date();
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get( calendar.MONTH)+1;
        int year = calendar.get(calendar.YEAR);
        calendar.set(year, month - 1, 1);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        date = calendar.getTime();
        String lastDayDateThisMonth = dateFormat.format(date);
        String query = "SELECT * FROM JobForms JOIN Users ON JobForms.CustomerId = Users.CustomerId"+
                " WHERE JobForms.EndDate >= '"+today+"' AND JobForms.EndDate<= '" +lastDayDateThisMonth + "'"+
                "AND Users.Rating >= "+minRate + " AND Users.Rating <= "+maxRate+
                " AND JobForms.StatusId = 3";
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

                query = "SELECT TOP 1 ImageBytes FROM IMAGES WHERE JobFormId = "+jobForm.ID;
                ResultSet imageResultSet = ExecuteSelectQuery(query);
                if(imageResultSet.next()){
                    jobForm.ImageBytes  = imageResultSet.getBytes("ImageBytes");
                }
            }

        }catch (Exception ex){

        }
        return jobForms;
    }

    /**
     * Get All JobForms by publisher rating and date range
     * date range set by tab selected index.
     * @param minRate
     * @param maxRate
     * @param tabSelected
     * @return
     */
    public List<JobForm> GetJobFormsByPublisherRating (int minRate ,int maxRate,int tabSelected ){
        String query = "SELECT * FROM JobForms JOIN Users on JobForms.CustomerId = Users.CustomerId Where Users.Rating >= " +
                minRate + " and Users.Rating <= " + maxRate+
                " AND JobForms.StatusId = 3";

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String minDate = dateFormat.format(date);
        Calendar calendar = Calendar.getInstance();
        String maxDate;
        switch (tabSelected){
            case 0:
                query += " AND EndDate >= '"+minDate+"'";
                break;
            case 1:
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                calendar.add(Calendar.DATE, 7-dayOfWeek);
                date = calendar.getTime();
                maxDate = dateFormat.format(date);
                query += " AND EndDate >= '"+minDate+"' AND JobForms.EndDate<= '" +maxDate + "'";
                break;
            case 2:
                int month = calendar.get( calendar.MONTH)+1;
                int year = calendar.get(calendar.YEAR);
                calendar.set(year, month - 1, 1);
                calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
                date = calendar.getTime();
                maxDate =dateFormat.format(date);
                query += " AND EndDate >= '"+minDate+"' AND JobForms.EndDate<= '" +maxDate + "'";
                break;
        }

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

                query = "SELECT TOP 1 ImageBytes FROM IMAGES WHERE JobFormId = "+jobForm.ID;
                ResultSet imageResultSet = ExecuteSelectQuery(query);
                if(imageResultSet.next()){
                    jobForm.ImageBytes  = imageResultSet.getBytes("ImageBytes");
                }
            }

        }catch (Exception ex){

        }
        return jobForms;
    }

    /**
     * Get JobFrom Object By ID
     * @param jobFormId
     * @return
     */
    public JobForm GetJobFormById (int jobFormId){
        String query = "SELECT * FROM JobForms WHERE ID = " + jobFormId;

        JobForm jobForm = null;
        try{
            ResultSet result = ExecuteSelectQuery(query);
            if (result.next()){
                jobForm = new JobForm(
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

                jobForm.AllImagesBytes = new ArrayList<byte[]>();

                query = "SELECT ImageBytes FROM IMAGES WHERE JobFormId = "+jobFormId;
                ResultSet imageResultSet = ExecuteSelectQuery(query);
                while (imageResultSet.next()){
                    jobForm.AllImagesBytes.add(imageResultSet.getBytes("ImageBytes"));
                }
            }

        }catch (Exception ex){

        }

        return jobForm;
    }

    /**
     * Inser a new JobForm
     * @param jobForm
     * @param StartDate
     * @param EndDate
     * @return
     */
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

    /**
     * insert bitmap image for new jobform.
     * @param jobFormId
     * @param bitmap
     * @return
     */
    public boolean InsertImage(int jobFormId , Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();

        String query = "INSERT INTO Images(JobFormId,ImageBytes) Values(?,?)";
        try {
            PreparedStatement pst = _connection.prepareStatement(query);
            pst.setInt(1,jobFormId);
            pst.setBytes(2,b);
            int row = pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }


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
                        Util.GetProperty("db.driver", context1),
                        Util.GetProperty("db.url", context1),
                        Util.GetProperty("db.username", context1),
                        Util.GetProperty("db.password", context1));
            } catch (Exception ex) {
                Toast.makeText(context1, "אין חיבור", Toast.LENGTH_SHORT).show();
            }
        }
        else if (instance.getConnection().isClosed()){
            try {
                instance = new ApplicationDbContext(
                        Util.GetProperty("db.driver", context1),
                        Util.GetProperty("db.url", context1),
                        Util.GetProperty("db.username", context1),
                        Util.GetProperty("db.password", context1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
}
