package Models;

import java.util.ArrayList;

public class Employee {

    public int ID;
    public String Firstname;
    public String Lastname;
    public String Email;
    public String Phone;
    public ArrayList<Float> ratings = new ArrayList<>();
    public float Rating;

    public Employee (int ID,String Firstname,String Lastname,String Email,String Phone){
        this.ID = ID;
        this.Firstname = Firstname;
        this.Lastname = Lastname;
        this.Email = Email;
        this.Phone = Phone;
    }

    public Employee(){}

}
