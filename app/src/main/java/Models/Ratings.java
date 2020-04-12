package Models;

public class Ratings {

    public int ID;
    public int From;
    public int To;
    public float Rating;


    public Ratings(int from, int to, float rating){
        From = from;
        To = to;
        Rating = rating;
    }

    public Ratings(){}
}
