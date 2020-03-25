package Models;

import net.sourceforge.jtds.jdbc.DateTime;

import java.util.Date;
import java.util.List;

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
    public String Description;

    public Customer customer;
    public Status status;
    public byte[] ImageBytes;
    public List<byte[]> AllImagesBytes;

    public JobForm (int ID,int CustomerId,int Rooms,String City,String Address,float Budget, Date StartDate,Date EndDate,int StatusId,String Description){
        this.ID = ID;
        this.CustomerId = CustomerId;
        this.Rooms = Rooms;
        this.City = City;
        this.Address = Address;
        this.Budget = Budget;
        this.StartDate = StartDate;
        this.EndDate = EndDate;
        this.StatusId = StatusId;
        this.Description = Description;
    }

    public JobForm (int CustomerId,int Rooms,String City,String Address,float Budget,String Description){
        this.CustomerId = CustomerId;
        this.Rooms = Rooms;
        this.City = City;
        this.Address = Address;
        this.Budget = Budget;
        this.Description = Description;
    }

    public JobForm(){}
}
