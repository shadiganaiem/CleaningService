package Models;

public class Customer{

    public int ID;
    public String Firstname;
    public String Lastname;
    public String Email;
    public String Phone;

    public Customer (int ID,String Firstname,String Lastname,String Email,String Phone){
        this.ID = ID;
        this.Firstname = Firstname;
        this.Lastname = Lastname;
        this.Email = Email;
        this.Phone = Phone;
    }

    public Customer(){}

}
