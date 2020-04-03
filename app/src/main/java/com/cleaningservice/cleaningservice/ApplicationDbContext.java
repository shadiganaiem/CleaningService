package com.cleaningservice.cleaningservice;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import Models.Request;

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

import Models.Customer;
import Models.Employee;
import Models.JobForm;
import Models.JobFormRequest;
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
     * get employee id by user id
     * @param id
     * @return
     */
    public int GetEmployeeIdByUserID(int id){
        String query = "SELECT EmployeeId FROM USERS WHERE ID="+id;
        try{
            ResultSet result = ExecuteSelectQuery(query);
            if (result.next()){
                int employeeId = result.getInt("EmployeeId");

                return employeeId;
            }
        }
        catch (Exception ex){

        }
        return 0;
    }

    /**
     * Get All JobForms
     * @return
     */
    public List<JobForm> GetJobForms(int minRate,int maxRate){
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String today = dateFormat.format(date);

        String query = "SELECT J.Id , J.CustomerId ,  J.Rooms , J.City,  J.Address,  J.Budget," +
                " J.StartDate,J.EndDate, J.StatusId, J.Description, C.Firstname , C.Lastname , U.Rating" +
                " FROM JobForms AS J JOIN Users AS U ON J.CustomerId = U.CustomerId"+
                " JOIN Customers AS C ON C.ID = U.CustomerId "+
                " WHERE J.EndDate>= '"+today+"' AND U.Rating >= "+minRate + " AND U.Rating <= "+maxRate+
                " AND J.StatusId = 3";
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
        String query = "SELECT J.Id , J.CustomerId ,  J.Rooms , J.City,  J.Address,  J.Budget, J.StartDate,J.EndDate," +
                " J.StatusId, J.Description, C.Firstname , C.Lastname , U.Rating " +
                " FROM JobForms AS J JOIN USERS AS U ON J.CustomerId = U.CustomerId" +
                " JOIN Customers ON C C.ID = U.CustomerId " +
                " WHERE J.StartDate < '"+lastDateInThisWeek+"' AND J.EndDate>= '" +today + "'"+
                " AND U.Rating >= "+minRate + " AND U.Rating <= "+maxRate+
                " AND J.StatusId = 3";
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
        String query = "SELECT J.Id , J.CustomerId ,  J.Rooms , J.City,  J.Address,  J.Budget," +
                " J.StartDate,J.EndDate, J.StatusId, J.Description, C.Firstname , C.Lastname , U.Rating" +
                " FROM JobForms AS J JOIN Users AS U ON J.CustomerId = U.CustomerId"+
                " JOIN Customers AS C ON C.ID = U.CustomerId"+
                " WHERE J.EndDate >= '"+today+"' AND J.EndDate<= '" +lastDayDateThisMonth + "'"+
                "AND U.Rating >= "+minRate + " AND U.Rating <= "+maxRate+
                " AND J.StatusId = 3";
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
        String query = "SELECT J.Id , J.CustomerId , J.Rooms , J.City, J.Address, J.Budget, " +
                "J.StartDate,J.EndDate, J.StatusId, J.Description, C.Firstname , C.Lastname , U.Rating " +
                "FROM JobForms AS J JOIN Users AS U on J.CustomerId = U.CustomerId " +
                "JOIN Customers AS C ON C.ID = U.CustomerId " +
                "Where U.Rating >= " + minRate + " and U.Rating <= " + maxRate+
                " AND J.StatusId = 3";

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
                        result.getInt("StatusId"),
                        result.getString("Description")
                );
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
                        result.getInt("StatusId"),
                        result.getString("Description")

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
     * Get JobForms By Customer ID
     * @return
     */
    public List<Integer> GetJobFormID(User user){

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
    }

    /**
     * Inser a new JobForm
     * @param jobForm
     * @param StartDate
     * @param EndDate
     * @return
     */
    public int InsertJobForm(JobForm jobForm,String StartDate,String EndDate){
        String query = "INSERT INTO JobForms(CustomerId,Rooms,City,Address,Budget,StartDate,EndDate,StatusId,Description)";
        query += "VALUES("+jobForm.CustomerId + ","+jobForm.Rooms + ",'"+jobForm.City.replace("'","")+"','"+jobForm.Address.replace("'","") + "',"+
                jobForm.Budget + ",'"+StartDate + "','"+EndDate+"',"+3+",'"+jobForm.Description+"')";

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
        String query = "INSERT INTO JobFormRequests(EmployeeId,JobFormId,StatusId)"
                +" VALUES("+jobFormRequest.EmployeeId+","+jobFormRequest.JobFormId +",5)";

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

    public ArrayList<Request> GetFormUserRequests (int jobFormId) throws SQLException {

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
                             img
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
    public boolean UpdateRequestStatus(int employeeId, int statusId){

        String query = "UPDATE JobFormRequests SET StatusId = ? WHERE EmployeeId = ?";

        try {
            PreparedStatement pst = _connection.prepareStatement(query);
            pst.setInt(1,statusId);
            pst.setInt(2,employeeId);
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
