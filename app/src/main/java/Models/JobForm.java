package Models;

import java.util.Date;
import java.util.List;

public class JobForm {
    public int ID;
    public int Rooms;
    public String City;
    public Date EndDate;
    public float Budget;
    public int StatusId;
    public int CustomerId;
    public Date StartDate;
    public String Address;
    public String Description;
    public Date CreationDate;
    public Customer customer;
    public Status status;
    public byte[] ImageBytes;
    public List<byte[]> AllImagesBytes;
    public String EmployeeName;
    public int EmployeeID;

    public JobForm (int ID,int CustomerId,int Rooms,String City,String Address,float Budget, Date StartDate,Date EndDate,int StatusId,String Description){
        this.ID = ID;
        this.City = City;
        this.Rooms = Rooms;
        this.Budget = Budget;
        this.EndDate = EndDate;
        this.Address = Address;
        this.StatusId = StatusId;
        this.StartDate = StartDate;
        this.CustomerId = CustomerId;
        this.Description = Description;
    }

    public JobForm (int CustomerId,int Rooms,String City,String Address,float Budget,String Description){
        this.City = City;
        this.Rooms = Rooms;
        this.Budget = Budget;
        this.Address = Address;
        this.CustomerId = CustomerId;
        this.Description = Description;
    }

    public JobForm(){}
}
