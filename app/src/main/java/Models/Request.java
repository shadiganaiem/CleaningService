package Models;

public class Request {
    public String FirstName;
    public String LastName;
    public String Phone;
    public String Email;
    public byte[] ImageBytes;
    public int Rating;

    public Request(String firstName ,String lastName, String phone, String email, int rating,byte[] image){
        FirstName=firstName;
        LastName=lastName;
        Phone=phone;
        Email=email;
        ImageBytes = image;
        Rating=rating;

    }
}


