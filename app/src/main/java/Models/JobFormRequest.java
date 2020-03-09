package Models;

public class JobFormRequest {

    public int ID;
    public int EmployeeId;
    public int JobFormId;
    public int StatusId;

    public Employee employee;
    public JobForm jobForm;
    public Status status;

    public JobFormRequest(int employeeId,int jobFormId ,int statusId){
        EmployeeId = employeeId;
        JobFormId = jobFormId;
        StatusId = statusId;
    }

    public JobFormRequest(){}
}
