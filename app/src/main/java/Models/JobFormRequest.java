package Models;

import java.util.Date;

public class JobFormRequest {

    public int ID;
    public int EmployeeId;
    public int JobFormId;
    public int StatusId;
    public Date CreationDate;

    public Employee employee;
    public JobForm jobForm;
    public Status status;

    public JobFormRequest(int employeeId,int jobFormId){
        EmployeeId = employeeId;
        JobFormId = jobFormId;
    }

    public JobFormRequest(){}

    public Date GetCreaionDate(){
        return this.CreationDate;
    }
}
