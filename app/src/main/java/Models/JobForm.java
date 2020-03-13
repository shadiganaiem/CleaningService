package Models;

import net.sourceforge.jtds.jdbc.DateTime;

import java.util.Date;

public class JobForm {
    public int ID;
    public int CustomerId;
    public int Rooms;
    public String City;
    public String Address;
    public float Budget;
    public Date StartDate;
    public Date EndDate;
    public int StatusId;

    public Customer customer;
    public Status status;
    public byte[] ImageBytes;

    public JobForm (int ID,int CustomerId,int Rooms,String City,String Address,float Budget, Date StartDate,Date EndDate,int StatusId){
        this.ID = ID;
        this.CustomerId = CustomerId;
        this.Rooms = Rooms;
        this.City = City;
        this.Address = Address;
        this.Budget = Budget;
        this.StartDate = StartDate;
        this.EndDate = EndDate;
        this.StatusId = StatusId;
    }

    public JobForm (int CustomerId,int Rooms,String City,String Address,float Budget){
        this.CustomerId = CustomerId;
        this.Rooms = Rooms;
        this.City = City;
        this.Address = Address;
        this.Budget = Budget;
        this.StartDate = StartDate;
        this.EndDate = EndDate;
    }

    public JobForm(){}
}
