package Models;




public class Request {
    public String FirstName;
    public String LastName;
    private String Phone;
    public int EmployeeId;
    public String Email;
    public byte[] ImageBytes;
    public int Rating;
    public int Status_id;

    public Request(int id, int status_id, String firstName ,String lastName, String phone, String email, int rating,byte[] image){
        FirstName=firstName;
        LastName=lastName;
        Phone=phone;
        Email=email;
        ImageBytes = image;
        Rating=rating;
        Status_id =status_id;
        EmployeeId =id;
    }


}


