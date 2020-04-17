package com.cleaningservice.cleaningservice;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cleaningservice.cleaningservice.Customer.NameImage;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Models.Customer;
import Models.Employee;
import Models.Favorite;
import Models.JobForm;
import Models.JobFormRequest;
import Models.Ratings;
import Models.Request;
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
     * Get User Details
     * @param id
     * @return
     */
    public User UserDetails(int id){
        String query = "SELECT Username , Password , ActivationCode, ID , CustomerId, EmployeeId,StatusId FROM USERS WHERE ID = " + id;
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


    public byte[] GetProfileImage(int userId){
        String query = "SELECT Image FROM USERS WHERE ID = "+userId;

        try{
            ResultSet result = ExecuteSelectQuery(query);
            if(result.next())
                return result.getBytes("Image");
        }
        catch (Exception ex){

        }
        return null;
    }

    /**
     * GET USER ID
     * @param username
     * @return
     */
    public int GetUserIdByUsername(String username){
        String query = "SELECT ID FROM USERS WHERE Username = " + username;
        try{
            ResultSet result = ExecuteSelectQuery(query);
            if (result.next()) {
                return result.getInt("ID");
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return 0 ;
    }

    /**
     * get employee id by user id
     * @param id
     * @return
     */
    public int GetEmployeeIdByUserID(int id){
        String query = "SELECT EmployeeId FROM USERS WHERE ID="+id;
        try {
            ResultSet result = ExecuteSelectQuery(query);
            if (result.next()) {

                return result.getInt("EmployeeId");
            }
        } catch (SQLException ignored) {

        }
        return 0;
    }

    /**
     * get customer id by user id
     * @param id
     * @return
     */
    public int GetCustomerIdByUserID(int id){
        String query = "SELECT CustomerId FROM USERS WHERE ID="+id;
        try {
            ResultSet result = ExecuteSelectQuery(query);
            if (result.next()) {

                return result.getInt("CustomerId");
            }
        } catch (SQLException ignored) {

        }
        return 0;
    }

    /**
     * get employee id by user id
     * @param id
     * @return
     */
    public int GetUserIDByEmployeeID(int id){
        String query = "SELECT ID FROM USERS WHERE EmployeeId ="+id;
        try {
            ResultSet result = ExecuteSelectQuery(query);
            if (result.next()) {
                return result.getInt("ID");
            }
        }
        catch (SQLException ignored) {
        }
        return 0;
    }

    /**
     * get employee id by user id
     * @param id
     * @return
     */
    public int GetUserIDByCustomerID(int id){
        String query = "SELECT ID FROM USERS WHERE CustomerId ="+id;
        try {
            ResultSet result = ExecuteSelectQuery(query);
            if (result.next()) {
                return result.getInt("ID");
            }
        }
        catch (SQLException ignored) {
        }
        return 0;
    }

    /**
     * Get All Employee Jobs Requests that Includes Job Ends
     * @param employeeId
     * @return
     */
    public List<JobFormRequest> GetEmployeeEndedJobFormsRequests(int employeeId){
        String query = "SELECT JFR.JobFormId , JFR.StatusId ,JFR.CreationDate, JF.ID,JF.EndDate, JF.CustomerId,JF.City, JF.Address,JF.StatusId AS JStatusId, C.Firstname, C.Lastname, C.Phone, U.Rating" +
                " FROM JobFormRequests AS JFR" +
                " JOIN JobForms AS JF ON JF.ID = JFR.JobFormId" +
                " JOIN Customers AS C ON C.ID = JF.CustomerId" +
                " JOIN Users AS U ON U.CustomerId = C.ID" +
                " WHERE JFR.EmployeeId = " + employeeId+
                " AND JFR.StatusId = " + Util.Statuses.ACCEPTED.value+
                " AND JF.StatusId = " + Util.Statuses.CLOSED.value;

        List<JobFormRequest> requests = new ArrayList<>();
        try {
            ResultSet result = ExecuteSelectQuery(query);

            while (result.next()) {
                JobFormRequest request = new JobFormRequest();
                request.EmployeeId = employeeId;
                request.JobFormId = result.getInt("JobFormId");
                request.StatusId = result.getInt("StatusId");
                request.CreationDate = result.getDate("CreationDate");
                JobForm jobForm = new JobForm();
                jobForm.ID = result.getInt("ID");
                jobForm.CustomerId = result.getInt("CustomerId");
                jobForm.City = result.getString("City");
                jobForm.Address = result.getString("Address");
                jobForm.EndDate = result.getDate("EndDate");
                jobForm.StatusId = result.getInt("JStatusId");
                Customer customer = new Customer();

                customer.Firstname = result.getString("Firstname");
                customer.Lastname = result.getString("Lastname");
                customer.Phone = result.getString("Phone");
                customer.Rating = result.getInt("Rating");

                jobForm.customer = customer;
                request.jobForm = jobForm;

                requests.add(request);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return requests;
    }

    /**
     * Get All JobForms
     * @return
     */
    public List<JobForm> GetJobForms(int minRate,int maxRate){
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
        Date date = new Date();
        String today = dateFormat.format(date);

        String query = "SELECT J.Id , J.CustomerId ,J.CreationDate,  J.Rooms , J.City,  J.Address,  J.Budget," +
                " J.StartDate,J.EndDate, J.StatusId, J.Description, C.Firstname , C.Lastname , U.Rating" +
                " FROM JobForms AS J JOIN Users AS U ON J.CustomerId = U.CustomerId"+
                " JOIN Customers AS C ON C.ID = U.CustomerId "+
                " WHERE J.EndDate>= '"+today+"' AND U.Rating >= "+minRate + " AND U.Rating <= "+maxRate+
                " AND J.StatusId = " + Util.Statuses.AVAILABLE.value;
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
                        result.getInt("StatusId"),
                        result.getString("Description")
                );
                jobForm.CreationDate = result.getDate("CreationDate");
                jobForm.customer = new Customer();
                jobForm.customer.Lastname = result.getString("Lastname");
                jobForm.customer.Firstname = result.getString("Firstname");
                jobForm.customer.Rating = result.getInt("Rating");
                jobForm.status = GetStatus(jobForm.StatusId);

                jobForms.add(jobForm);

                /*
                query = "SELECT TOP 1 ImageBytes FROM IMAGES WHERE JobFormId = "+jobForm.ID;
                ResultSet imageResultSet = ExecuteSelectQuery(query);
                if(imageResultSet.next()){
                    jobForm.ImageBytes  = imageResultSet.getBytes("ImageBytes");
                }
                */
            }

        }catch (Exception ex){

        }
        return jobForms;
    }

    /**
     * Get Employee Requests List
     * @param employeeId
     * @return
     */
    public List<JobFormRequest> GetEmployeeResponsedJobRequests(int employeeId){

        String query = "SELECT JFR.JobFormId , JFR.StatusId ,JFR.CreationDate, JF.ID,JF.EndDate, JF.CustomerId,JF.City, JF.Address,JF.StatusId AS JStatusId, C.Firstname, C.Lastname, C.Phone, U.Rating" +
                " FROM JobFormRequests AS JFR" +
                " JOIN JobForms AS JF ON JF.ID=JFR.JobFormId" +
                " JOIN Customers AS C ON C.ID = JF.CustomerId"+
                " JOIN Users AS U ON U.CustomerId = C.ID" +
                " WHERE JFR.EmployeeId = " + employeeId +
                " AND ( JFR.StatusId = " +Util.Statuses.ACCEPTED.value+  " OR JFR.StatusId = "+Util.Statuses.REJECTED.value+")" +
                " AND JF.StatusId != "+Util.Statuses.CLOSED.value ;
        List<JobFormRequest> requests = new ArrayList<>();
        try {
            ResultSet result = ExecuteSelectQuery(query);

            while (result.next()) {
                JobFormRequest request = new JobFormRequest();
                request.EmployeeId = employeeId;
                request.JobFormId = result.getInt("JobFormId");
                request.StatusId = result.getInt("StatusId");
                request.CreationDate = result.getDate("CreationDate");
                JobForm jobForm = new JobForm();
                jobForm.ID = result.getInt("ID");
                jobForm.CustomerId = result.getInt("CustomerId");
                jobForm.City = result.getString("City");
                jobForm.Address = result.getString("Address");
                jobForm.EndDate = result.getDate("EndDate");
                jobForm.StatusId = result.getInt("JStatusId");
                Customer customer = new Customer();

                customer.Firstname = result.getString("Firstname");
                customer.Lastname = result.getString("Lastname");
                customer.Phone = result.getString("Phone");
                customer.Rating = result.getInt("Rating");

                jobForm.customer = customer;
                request.jobForm = jobForm;

                requests.add(request);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return requests;
    }

    /**
     * Get All Waiting Requests for specific employee
     * @param employeeId
     * @return
     */
    public List<JobFormRequest> GetEmployeeRequestsByStatus(int employeeId,Util.Statuses status){
        String query = "SELECT JFR.JobFormId , JFR.StatusId ,JFR.CreationDate, JF.ID,JF.EndDate, JF.CustomerId,JF.City, JF.Address,JF.StatusId AS JStatusId, C.Firstname, C.Lastname, C.Phone, U.Rating" +
                " FROM JobFormRequests AS JFR" +
                " JOIN JobForms AS JF ON JF.ID=JFR.JobFormId" +
                " JOIN Customers AS C ON C.ID = JF.CustomerId"+
                " JOIN Users AS U ON U.CustomerId = C.ID" +
                " WHERE JFR.EmployeeId = " + employeeId +
                " AND JFR.StatusId = " +status.value+
                " AND JF.StatusId != " +Util.Statuses.CLOSED.value;
        List<JobFormRequest> requests = new ArrayList<>();
        try {
            ResultSet result = ExecuteSelectQuery(query);

            while (result.next()) {
                JobFormRequest request = new JobFormRequest();
                request.EmployeeId = employeeId;
                request.JobFormId = result.getInt("JobFormId");
                request.StatusId = result.getInt("StatusId");
                request.CreationDate = result.getDate("CreationDate");
                JobForm jobForm = new JobForm();
                jobForm.ID = result.getInt("ID");
                jobForm.CustomerId = result.getInt("CustomerId");
                jobForm.City = result.getString("City");
                jobForm.Address = result.getString("Address");
                jobForm.EndDate = result.getDate("EndDate");
                jobForm.StatusId = result.getInt("JStatusId");
                Customer customer = new Customer();

                customer.Firstname = result.getString("Firstname");
                customer.Lastname = result.getString("Lastname");
                customer.Phone = result.getString("Phone");
                customer.Rating = result.getInt("Rating");

                jobForm.customer = customer;
                request.jobForm = jobForm;

                requests.add(request);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return requests;
    }

    /**
     * get all job forms for this week.
     * @return
     */
    public List<JobForm> GetJobFormsForThisWeek(int minRate, int maxRate){
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
        Date date = new Date();
        String today = dateFormat.format(date);
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        c.add(Calendar.DATE, 7-dayOfWeek);
        date = c.getTime();
        String lastDateInThisWeek = dateFormat.format(date);
        String query = "SELECT J.Id , J.CustomerId ,J.CreationDate,  J.Rooms , J.City,  J.Address,  J.Budget, J.StartDate,J.EndDate," +
                " J.StatusId, J.Description, C.Firstname , C.Lastname , U.Rating " +
                " FROM JobForms AS J JOIN USERS AS U ON J.CustomerId = U.CustomerId" +
                " JOIN Customers AS C ON C.ID = U.CustomerId " +
                " WHERE J.StartDate < '"+lastDateInThisWeek+"' AND J.EndDate>= '" +today + "'"+
                " AND U.Rating >= "+minRate + " AND U.Rating <= "+maxRate+
                " AND J.StatusId = " + Util.Statuses.AVAILABLE.value;
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
                        result.getInt("StatusId"),
                        result.getString("Description")
                );
                jobForm.CreationDate = result.getDate("CreationDate");
                jobForm.customer = new Customer();
                jobForm.customer.Lastname = result.getString("Lastname");
                jobForm.customer.Firstname = result.getString("Firstname");
                jobForm.customer.Rating = result.getInt("Rating");
                jobForm.status = GetStatus(jobForm.StatusId);

                jobForms.add(jobForm);

                /*
                query = "SELECT TOP 1 ImageBytes FROM IMAGES WHERE JobFormId = "+jobForm.ID;

                ResultSet imageResultSet = ExecuteSelectQuery(query);
                if(imageResultSet.next()){
                    jobForm.ImageBytes  = imageResultSet.getBytes("ImageBytes");
                }

                 */
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
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
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
        String query = "SELECT J.Id , J.CustomerId ,J.CreationDate,  J.Rooms , J.City,  J.Address,  J.Budget," +
                " J.StartDate,J.EndDate, J.StatusId, J.Description, C.Firstname , C.Lastname , U.Rating" +
                " FROM JobForms AS J JOIN Users AS U ON J.CustomerId = U.CustomerId"+
                " JOIN Customers AS C ON C.ID = U.CustomerId"+
                " WHERE J.EndDate >= '"+today+"' AND J.EndDate<= '" +lastDayDateThisMonth + "'"+
                "AND U.Rating >= "+minRate + " AND U.Rating <= "+maxRate+
                " AND J.StatusId = " + Util.Statuses.AVAILABLE.value;
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
                        result.getInt("StatusId"),
                        result.getString("Description")
                );
                jobForm.CreationDate = result.getDate("CreationDate");
                jobForm.customer = new Customer();
                jobForm.customer.Lastname = result.getString("Lastname");
                jobForm.customer.Firstname = result.getString("Firstname");
                jobForm.customer.Rating = result.getInt("Rating");
                jobForm.status = GetStatus(jobForm.StatusId);

                jobForms.add(jobForm);

               /*
               query = "SELECT TOP 1 ImageBytes FROM IMAGES WHERE JobFormId = "+jobForm.ID;

                ResultSet imageResultSet = ExecuteSelectQuery(query);
                if(imageResultSet.next()){
                    jobForm.ImageBytes  = imageResultSet.getBytes("ImageBytes");

                }

                */
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
        String query = "SELECT J.ID ,J.CreationDate, J.CustomerId , J.Rooms , J.City, J.Address, J.Budget, " +
                "J.StartDate,J.EndDate, J.StatusId, J.Description, C.Firstname , C.Lastname , U.Rating " +
                "FROM JobForms AS J " +
                "JOIN Customers AS C ON C.ID = J.CustomerId " +
                "JOIN Users AS U on C.ID = U.CustomerId " +
                "Where U.Rating >= " + minRate + " and U.Rating <= " + maxRate+
                " AND J.StatusId = " + Util.Statuses.AVAILABLE.value;

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
        Date date = new Date();
        String minDate = dateFormat.format(date);
        Calendar calendar = Calendar.getInstance();
        String maxDate;
        switch (tabSelected){
            case 0:
                query += " AND J.EndDate >= '"+minDate+"'";
                break;
            case 1:
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                calendar.add(Calendar.DATE, 7-dayOfWeek);
                date = calendar.getTime();
                maxDate = dateFormat.format(date);
                query += " AND J.EndDate >= '"+minDate+"' AND J.EndDate<= '" +maxDate + "'";
                break;
            case 2:
                int month = calendar.get( calendar.MONTH)+1;
                int year = calendar.get(calendar.YEAR);
                calendar.set(year, month - 1, 1);
                calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
                date = calendar.getTime();
                maxDate =dateFormat.format(date);
                query += " AND J.EndDate >= '"+minDate+"' AND J.EndDate<= '" +maxDate + "'";
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
                        result.getInt("StatusId"),
                        result.getString("Description")
                );

                jobForm.CreationDate = result.getDate("CreationDate");
                jobForm.customer = new Customer();
                jobForm.customer.Lastname = result.getString("Lastname");
                jobForm.customer.Firstname = result.getString("Firstname");
                jobForm.customer.Rating = result.getInt("Rating");
                jobForm.status = GetStatus(jobForm.StatusId);

                jobForms.add(jobForm);

                /*
                query = "SELECT TOP 1 ImageBytes FROM IMAGES WHERE JobFormId = "+jobForm.ID;
                ResultSet imageResultSet = ExecuteSelectQuery(query);
                if(imageResultSet.next()){
                    jobForm.ImageBytes  = imageResultSet.getBytes("ImageBytes");
                }

                 */
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
        String query = "SELECT J.ID , J.CustomerId , J.Rooms, J.City,J.Address, J.Budget, J.StartDate, J.EndDate, J.StatusId , J.Description , C.Firstname , C.Lastname, C.Email , C.Phone, U.Rating" +
                " FROM JobForms AS J " +
                " JOIN Customers AS C ON C.ID = J.CustomerId " +
                " JOIN USERS AS U ON U.CustomerId = C.ID" +
                " WHERE J.ID = " + jobFormId;

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
                        result.getInt("StatusId"),
                        result.getString("Description")

                );
                jobForm.customer = new Customer();
                jobForm.customer.ID = result.getInt("CustomerId" );
                jobForm.customer.Firstname = result.getString("Firstname");
                jobForm.customer.Lastname = result.getString("Lastname");
                jobForm.customer.Email = result.getString("Email");
                jobForm.customer.Phone = result.getString("Phone");
                jobForm.customer.Rating = result.getInt("Rating");
                jobForm.CreationDate = result.getDate("CreationDate");
                jobForm.status = GetStatus(jobForm.StatusId);
            }

        }catch (Exception ex){

        }

        return jobForm;
    }

    /**
     * Get JobForms By Customer ID
     * @return
     */
   /* public List<Integer> GetJobFormID(User user){

        String query = "SELECT ID FROM JobForms WHERE CustomerId = "+user.CustomerId;

        ArrayList<Integer> ids = new ArrayList<Integer>();
        try{

            ResultSet result = ExecuteSelectQuery(query);
            while (result.next()){

                ids.add(result.getInt("ID"));
            }

        }catch (Exception ex){

        }
        return ids;
    }*/

    /**
     * Get JobForms By Customer ID
     * @return
     */
    public List<Integer> GetJobFormID(int id){

        String query = "SELECT ID FROM JobForms WHERE CustomerId = "+id;

        ArrayList<Integer> ids = new ArrayList<Integer>();
        try{

            ResultSet result = ExecuteSelectQuery(query);
            while (result.next()){

                ids.add(result.getInt("ID"));
            }

        }catch (Exception ex){

        }
        return ids;
    }

    /**
     * Get JobForms By Customer ID
     * @return
     */
    public ArrayList<JobForm> GetJobByID(int id) throws SQLException {

        String query = "SELECT * FROM JobForms WHERE CustomerId = " + id;

        ArrayList<JobForm> jobForms = new ArrayList<>();

        ResultSet result = ExecuteSelectQuery(query);
        while (result.next()) {
            JobForm jobForm = new JobForm(
                    result.getInt("ID"),
                    result.getInt("CustomerId"),
                    result.getInt("Rooms"),
                    result.getString("City"),
                    result.getString("Address"),
                    result.getFloat("Budget"),
                    result.getDate("StartDate"),
                    result.getDate("EndDate"),
                    result.getInt("StatusId"),
                    result.getString("Description")

            );
            jobForms.add(jobForm);
        }
        return jobForms;
    }

    /**
     * Inser a new JobForm
     * @param jobForm
     * @param StartDate
     * @param EndDate
     * @return
     */
    public int InsertJobForm(JobForm jobForm,String StartDate,String EndDate){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
        Date date = new Date();

        String query = "INSERT INTO JobForms(CustomerId,Rooms,City,Address,Budget,StartDate,EndDate,StatusId,Description,CreationDate)";
        query += "VALUES("+jobForm.CustomerId + ","+jobForm.Rooms + ",'"+jobForm.City.replace("'","")+"','"+jobForm.Address.replace("'","") + "',"+
                jobForm.Budget + ",'"+StartDate + "','"+EndDate+"',"+Util.Statuses.AVAILABLE.value+",'"+jobForm.Description+"','"+dateFormat.format(date)+"')";

        int jobFormId = 0;
        if(ExecuteInsertData(query)){
            query = "SELECT ID FROM JobForms WHERE CustomerId = " + jobForm.CustomerId;
            try{
                ResultSet result = ExecuteSelectQuery(query);
                if (result.next())
                    jobFormId =  result.getInt("ID");
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }

        return jobFormId;
    }

    /**
     * Insert new Job Form Request
     * @param jobFormRequest
     * @return
     */
    public boolean InsertJobFormRequest(JobFormRequest jobFormRequest){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
        Date date = new Date();

        String query = "INSERT INTO JobFormRequests(EmployeeId,JobFormId,StatusId,CreationDate)"
                +" VALUES("+jobFormRequest.EmployeeId+","+jobFormRequest.JobFormId +","+Util.Statuses.WAITING.value+",'"+dateFormat.format(date)+"')";

        boolean result = ExecuteInsertData(query);
        return result;
    }

    /**
     * Insert job request
     * @param CustomerId
     * @param FormId
     * @return
     */
    public boolean InsertCustomerJobFormRequest(int CustomerId , int FormId){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
        Date date = new Date();

        String query = "INSERT INTO JobFormRequests(JobFormId,StatusId,CreationDate,CustomerId)"
                +" VALUES("+FormId +","+Util.Statuses.WAITING.value+",'"+dateFormat.format(date)+"',"+CustomerId+")";

        boolean result = ExecuteInsertData(query);
        return result;
    }

    /**
     * Check If JobForm Requested By Loggedin Employee.
     * @param jobFormId
     * @param employeeId
     * @return
     */
    public boolean IfJobFormRequested(int jobFormId,int employeeId){
        String query = "SELECT ID FROM JobFormRequests WHERE JobFormId = " + jobFormId +
                " AND EmployeeId = " + employeeId;

        boolean isRequested = false;
        try {
            ResultSet result = ExecuteSelectQuery(query);
            if (result.next())
                isRequested = true;

        }
        catch (Exception ex){

        }

        return isRequested;
    }

    /**
     * Get User Object By username
     * @param username
     * @return
     */
    public User GetUser(String username){
        String query  = "SELECT * FROM Users WHERE Username='"+username+"'";
        try {
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

    public NameImage GetNameImage(int jobFormId) throws SQLException {

        String query = "SELECT Employees.ID , Employees.Firstname , Employees.Lastname FROM JobFormRequests JOIN Employees on JobFormRequests.EmployeeId = Employees.ID " +
                "WHERE JobFormRequests.JobFormId = " + jobFormId + "AND JobFormRequests.StatusId = 6";

        NameImage nameimage = null;
        ResultSet result = ExecuteSelectQuery(query);
        if (result.next()) {

            String fn = result.getString("Firstname");
            String ln = result.getString("Lastname");
            int ID = result.getInt("ID");

            String fullname = fn + " " + ln;
           // String query3 = "SELECT Image From USERS WHERE EmployeeId = " + ID;
           // ResultSet result3 = ExecuteSelectQuery(query3);
           // if (result3.next()) {
              //  byte[] img = result3.getBytes("Image");
                nameimage = new NameImage(
                        fullname,
                        ID
                );
               // }
            }
            return nameimage;
    }

    public ArrayList<Request> GetFormUserRequests(int jobFormId) throws SQLException {

        String query = "SELECT * FROM JobFormRequests JOIN Employees on JobFormRequests.EmployeeId = Employees.ID " +
                "WHERE JobFormRequests.JobFormId = " + jobFormId;
        ArrayList<Request> requests = new ArrayList<>();


            ResultSet result = ExecuteSelectQuery(query);
            while (result.next()){

                int id= result.getInt("EmployeeId");
                int statusid= result.getInt("StatusId");
                String fn=result.getString("Firstname");
                String ln =result.getString("Lastname");
                String email=result.getString("Email");
                String phone =result.getString("Phone");


                 String query2  = "SELECT Rating FROM Users WHERE EmployeeId = "+ id;
                 String query3 = "SELECT Image From USERS WHERE EmployeeId = "+ id;
                 ResultSet result2 = ExecuteSelectQuery(query2);
                 ResultSet result3 = ExecuteSelectQuery(query3);
                 if (result2.next() && result3.next()){
                     int rate = result2.getInt("Rating");
                     byte[] img= result3.getBytes("Image");
                     Request request = new Request(
                             id,
                             statusid,
                             fn,
                             ln,
                             phone,
                             email,
                             rate,
                             img,
                             jobFormId
                     );
                     requests.add(request);
                 }

            }


        return requests;
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
     * @param bitmaps
     * @return
     */
    public boolean InsertImage(int jobFormId , List<Bitmap> bitmaps){

        if(jobFormId ==0 )
            return false;

        for(int i=0;i<bitmaps.size();i++){
            Bitmap bitmap = bitmaps.get(i);
            ByteArrayOutputStream baos=new  ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
            byte [] b=baos.toByteArray();

            String query = "INSERT INTO Images(JobFormId,ImageBytes) Values(?,?)";
            try {
                PreparedStatement pst = _connection.prepareStatement(query);
                pst.setInt(1,jobFormId);
                pst.setBytes(2,b);
                pst.executeUpdate();
            } catch (SQLException e) {
               return false;
            }
        }
        return true;
    }

    /**
     * Update User Profile Image.
     * @param userId
     * @param bitmap
     * @return
     */
    public boolean UpdateProfileImage(int userId,Bitmap bitmap){
        String query = "UPDATE USERS SET Image = ? WHERE ID = ?";

        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();

        try {
            PreparedStatement pst = _connection.prepareStatement(query);
            pst.setBytes(1,b);
            pst.setInt(2,userId);
            pst.executeUpdate();
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get Profile Image By Customer Id
     * @param customerId
     * @return
     */
    public byte[] GetProfileImageByCustomerId (int customerId){
        String query = "SELECT Image From USERS WHERE CustomerId = " +customerId;

        try{
            ResultSet result= ExecuteSelectQuery(query);
            if(result.next()){
                return result.getBytes("Image");
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Get DEFAULT PROFILE IMAGE FROM DATABASE ( USER ID = 11 )
     * @return
     */
    public byte[] GetDefaultProfileImage(){
        String query = "SELECT Image From USERS WHERE ID = 11";

        try{
            ResultSet resultSet = ExecuteSelectQuery(query);
            if(resultSet.next())
                return resultSet.getBytes("Image");
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * INITIALIZE DEFAULT PROFILE IMAGE FOR NEW USER
     * @param userId
     * @return
     */
    public boolean InitializeUserImage(int userId){

        String query = "UPDATE USERS SET Image = ? WHERE ID = ?";
        byte[] image = GetDefaultProfileImage();

        try {
            PreparedStatement pst = _connection.prepareStatement(query);
            pst.setBytes(1,image);
            pst.setInt(2,userId);
            pst.executeUpdate();
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *  update request
     * @param employeeId
     * @return
     */
    public boolean UpdateRequestStatus(int employeeId,int formID, int statusId){

        String query = "UPDATE JobFormRequests SET StatusId = ? WHERE EmployeeId = ? AND JobFormId = ?";

        try {
            PreparedStatement pst = _connection.prepareStatement(query);
            pst.setInt(1,statusId);
            pst.setInt(2,employeeId);
            pst.setInt(3,formID);
            pst.executeUpdate();
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean UpdateFormStatus(int formID, int statusId){

        String query = "UPDATE JobForms SET StatusId = ? WHERE ID = ?";
        try {
            PreparedStatement pst = _connection.prepareStatement(query);
            pst.setInt(1,statusId);
            pst.setInt(2,formID);
            pst.executeUpdate();
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * INSERT NEW RATING
     * @param ratings
     * @return true
     */
    public boolean InsertRating(Ratings ratings) throws SQLException {

        String query = "INSERT INTO Ratings([From],[To],Rating) VALUES('"+ratings.From+"','"+ratings.To+"','"+ratings.Rating+"')";
        return ExecuteInsertData(query);
    }

    /**
     * Get USER RATINGS
     * @param userId
     * @return
     * @throws SQLException
     */
    public ArrayList<Ratings> GetUserRatings(int userId) throws SQLException {
        ArrayList<Ratings> ratings = new ArrayList<>();

        String query = "SLEECT * FROM RATINGS AS R WHERE R.To = " + userId;

        ResultSet result = ExecuteSelectQuery(query);
        while (result.next()) {

            int id = result.getInt("ID");
            int from = result.getInt("From");
            int to = result.getInt("To");
            float Rating = result.getFloat("Rating");
            Ratings rate = new Ratings(from,to,Rating);
            rate.ID = id;

            ratings.add(rate);
        }

        return ratings;
    }

    /**
     * Get USER RATINGS
     * @param userId
     * @return
     * @throws SQLException
     */
    public ArrayList<Ratings> GetUserPublishRatings(int userId) throws SQLException {
        ArrayList<Ratings> ratings = new ArrayList<>();

        String query = "SLEECT * FROM RATINGS AS R WHERE R.From = " + userId;

        ResultSet result = ExecuteSelectQuery(query);
        while (result.next()) {

            int id = result.getInt("ID");
            int from = result.getInt("From");
            int to = result.getInt("To");
            float Rating = result.getFloat("Rating");
            Ratings rate = new Ratings(from,to,Rating);
            rate.ID = id;

            ratings.add(rate);
        }

        return ratings;
    }

    /**
     * Insert new Favorite Request
     * @param userId
     * @param favoriteUserId
     * @return
     */
    public boolean InsertFavoriteByUserId(int userId,int favoriteUserId){
        String query = "INSERT INTO FAVORITES(UserId,FavoriteUserId) VALUES('"+userId+"','"+favoriteUserId+"')";

        return ExecuteInsertData(query);
    }

    /**
     * Insert new Favorite Request
     * @param userId
     * @param employeeId
     * @return
     */
    public boolean InsertFavoriteByEmployeeId(int userId,int employeeId){
        String query = "INSERT INTO FAVORITES(UserId,FavoriteUserId) SELECT "+userId+", ID FROM USERS WHERE EmployeeId = "+employeeId;

        return ExecuteInsertData(query);
    }

    /**
     * Insert new FavoriteRequest
     * @param userId
     * @param customerId
     * @return
     */
    public boolean InsertFavoriteByCustomerId(int userId,int customerId){
        String query = "INSERT INTO FAVORITES(UserId,FavoriteUserId) SELECT "+userId+", ID FROM USERS WHERE CustomerId = "+customerId;

        return ExecuteInsertData(query);
    }

    /**
     * GET User Favorite Customers
     * @param userId
     * @return
     */
    public ArrayList<Favorite> GetUserFavoriteList(int userId,Util.UserTypes userType) {
        String query = "SELECT F.UserId,F.FavoriteUserId,E.Firstname,E.Lastname,E.Email,E.Phone,E.ID,F.ID AS FID,U.Rating,F.FavoriteUserId " +
                " FROM FAVORITES AS F" +
                " JOIN Users AS U ON U.ID = F.FavoriteUserId" +
                " JOIN "+userType.table+" AS E ON E.ID = U."+userType.relationId +
                " WHERE F.UserId = " + userId;

        ArrayList<Favorite> favorites = new ArrayList<>();
        try {
            ResultSet result = ExecuteSelectQuery(query);
            while (result.next()) {
                Favorite favorite = new Favorite();
                favorite.ID = result.getInt("FID");
                favorite.FavoriteUserId = result.getInt("FavoriteUserId");

                if(userType == Util.UserTypes.EMPLOYEE) {
                    Employee employee = new Employee();
                    employee.Firstname = result.getString("Firstname");
                    employee.Lastname = result.getString("Lastname");
                    employee.Email = result.getString("Email");
                    employee.Phone = result.getString("Phone");
                    employee.ID = result.getInt("ID");
                    employee.Rating = result.getInt("Rating");

                    favorite.employee = employee;
                }
                else
                {
                    Customer customer = new Customer();
                    customer.Firstname = result.getString("Firstname");
                    customer.Lastname = result.getString("Lastname");
                    customer.Email = result.getString("Email");
                    customer.Phone = result.getString("Phone");
                    customer.ID = result.getInt("ID");
                    customer.Rating = result.getInt("Rating");

                    favorite.customer = customer;
                }

                favorites.add(favorite);
            }
        } catch (Exception ex) {

        }
        return favorites;
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

    public boolean UpdateEmployeeRating(float rating) {
        return false;
    }


    /**
     * deletes form
     * @param id
     * @return
     */
    public boolean DeleteForm(int id) {
        try {
                deleteFormsAssossiations(id);
                PreparedStatement pst = _connection.prepareStatement("DELETE FROM JobForms WHERE ID = ?");
                pst.setInt(1, id);
                pst.executeUpdate();
                return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * deletes form relations
     * @param id
     */
    private void deleteFormsAssossiations(int id){
        try {
                deleteFormImages(id);
                PreparedStatement pst = _connection.prepareStatement("DELETE FROM JobFormRequests WHERE JobFormId = ?");
                pst.setInt(1, id);
                pst.executeUpdate();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * deletes form images
     * @param id
     */
    private void deleteFormImages(int id) {
        try {
            PreparedStatement pst = _connection.prepareStatement("DELETE FROM Images WHERE JobFormId = ?");
            pst.setInt(1, id);
            pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param FavoriteId
     * @return
     * @throws SQLException
     */
    public boolean DeleteFavorite(int FavoriteId) throws SQLException {
        PreparedStatement pst = _connection.prepareStatement("DELETE FROM Favorites WHERE ID = ?");
        pst.setInt(1, FavoriteId);
        pst.executeUpdate();
        return true;
    }


    /**
     * checkes is a user is already rated
     * @param rater
     * @param rated
     * @return
     * @throws SQLException
     */
    public boolean checkIfNotRated(int rater, int rated) {
        String query ="SELECT ID From RATINGS Where [From]="+rater+" And [To]="+rated;
        ResultSet result = ExecuteSelectQuery(query);
        try {
            if(result.next()){
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }




    /**
     * check if a user already added to favorites
     * @param adderId
     * @param addedId
     * @return
     * @throws SQLException
     */
    public boolean checkIfNotFavorite(int adderId, int addedId) {
        String query ="SELECT ID From Favorites Where UserId ="+adderId+" And FavoriteUserId="+addedId;
        ResultSet result = ExecuteSelectQuery(query);
        try {
            if(result.next()){
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Get All JobForm Images
     * @param jobFormId
     * @return
     */
    public ArrayList<byte[]> GetJobFormImages(int jobFormId){
        ArrayList<byte[]> AllImagesBytes = new ArrayList();
        String query = "SELECT ImageBytes FROM IMAGES WHERE JobFormId = "+jobFormId;

        try {
            ResultSet imageResultSet = ExecuteSelectQuery(query);
            while (imageResultSet.next()) {
                AllImagesBytes.add(imageResultSet.getBytes("ImageBytes"));
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return AllImagesBytes;
    }

    /**
     * DELETE USER FROM FAVORITE LIST
     * @return
     */
    public boolean DeleteUserFromFavorites(int id){
        String query = "DELETE FROM Favorites WHERE ID = "+ id;
        return ExecuteInsertData(query);
    }
}
