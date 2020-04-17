package Models;

import java.util.Date;

public class JobFormRequest {

    public int ID;
    public int EmployeeId;
    public int CustomerId;
    public int JobFormId;
    public int StatusId;
    public Date CreationDate;

    public Employee employee;
    public Customer customer;
    public JobForm jobForm;
    public Status status;



    public JobFormRequest(int userId, int jobFormId){
        EmployeeId = userId;
        JobFormId = jobFormId;
    }


    public JobFormRequest(){}

    public Date GetCreaionDate(){
        return this.CreationDate;
    }
}
