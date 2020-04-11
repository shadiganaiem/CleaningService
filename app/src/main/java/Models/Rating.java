package Models;

public class Rating {

    public int ID;
    public int From;
    public int To;
    public float Rating;


    public Rating(int from,int to,float rating){
        From = from;
        To = to;
        Rating = rating;
    }

    public  Rating(){}
}
