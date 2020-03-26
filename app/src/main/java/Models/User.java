package Models;

public class User {

    public String Username;
    public String ActivationCode;
    public String Password;
    public int ID;
    public int CustomerId;
    public int EmployeeId;
    public int StatusId;
    public byte[] Image;

    public Employee employee;
    public Customer customer;
    public Status status;

    public User(String Username,String Password,String ActivationCode,int ID,int CustomerId,int EmployeeId,int StatusId){
        this.Username = Username;
        this.Password = Password;
        this.ActivationCode = ActivationCode;
        this.ID = ID;
        this.CustomerId = CustomerId;
        this.EmployeeId = EmployeeId;
        this.StatusId = StatusId;
    }

    public User(){}
}
