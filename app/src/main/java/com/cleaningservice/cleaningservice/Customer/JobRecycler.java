package com.cleaningservice.cleaningservice.Customer;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cleaningservice.cleaningservice.ApplicationDbContext;
import com.cleaningservice.cleaningservice.R;
import com.cleaningservice.cleaningservice.Util;

import java.sql.SQLException;
import java.util.ArrayList;

import Models.Employee;
import Models.JobForm;
import Models.Ratings;

import static Authentications.Preferences.GetLoggedInUserID;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class JobRecycler extends RecyclerView.Adapter<JobRecycler.ViewHolder> {

    PopupWindow popUp;
    boolean click = true;
    private Context context;
    private ApplicationDbContext con = null;
    private  ArrayList<JobForm> jobForms = new ArrayList<>();
    private  ArrayList<NameImage> namesImages = new ArrayList<>();
    private  float[] ratings = new float[2];
    private TextView view;
    private Button cancelBut;
    private RatingBar bar;
    private RatingBar bar2;
    private Button rateBut;
    private TextView mail;


    public JobRecycler(Context context, ArrayList<JobForm> forms,ArrayList<NameImage> namesImages){
        this.jobForms =  forms;
        this.namesImages =namesImages;
        this.context = context;
    }

    @NonNull
    @Override
    public JobRecycler.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_job_forms,parent,false);
        JobRecycler.ViewHolder holder = new JobRecycler.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull JobRecycler.ViewHolder holder, int position) {


        try {
            con = ApplicationDbContext.getInstance(context.getApplicationContext());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        holder.startdate.setText(jobForms.get(position).StartDate.toString());
        holder.enddate.setText(jobForms.get(position).EndDate.toString());
        holder.formid.setText(String.valueOf(jobForms.get(position).ID));
        holder.city.setText(jobForms.get(position).City);
        holder.address.setText(jobForms.get(position).Address);
        holder.budget.setText(String.valueOf(jobForms.get(position).Budget));
        holder.rooms.setText(String.valueOf(jobForms.get(position).Rooms));


        if(jobForms.get(position).StatusId== Util.Statuses.NOTAVAILABLE.value) {
            holder.status.setText(R.string.Active);
            holder.name.setText(namesImages.get(position).name);
            try {
                if(!con.checkIfNotRated(GetLoggedInUserID(context), con.GetUserIDByEmployeeID(namesImages.get(position).ID))) {
                    holder.rate.setVisibility(View.INVISIBLE);
                }  else{
                    holder.rate.setVisibility(View.VISIBLE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


            try {
                if(!con.checkIfNotFavorite(GetLoggedInUserID(context), con.GetUserIDByEmployeeID(namesImages.get(position).ID))) {
                    holder.addtofav.setVisibility(View.INVISIBLE);
                }  else{
                    holder.addtofav.setVisibility(View.VISIBLE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //Glide.with(context).asBitmap().load(namesImages.get(position).image).into(holder.image);

            holder.addtofav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    con.InsertFavoriteByUserId(GetLoggedInUserID(context), con.GetUserIDByEmployeeID(namesImages.get(position).ID));
                    Toast.makeText(context,"נוסף למועדפים",Toast.LENGTH_SHORT).show();
                    holder.addtofav.setVisibility(View.INVISIBLE);
                }
            });
        }

        if(jobForms.get(position).StatusId== Util.Statuses.NOTAVAILABLE.value){

            holder.rate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                    View customView = inflater.inflate(R.layout.rating_pop_up,null);

                    popUp = new PopupWindow(
                            customView,
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            true
                    );
                    popUp.showAtLocation(holder.relativeLayout, Gravity.CENTER,0,0);


                    Employee employee = con.GetEmployee(namesImages.get(position).ID);
                    mail = customView.findViewById(R.id.mail);
                    view = customView.findViewById(R.id.fullname);
                    cancelBut = customView.findViewById(R.id.cancel);
                    rateBut = customView.findViewById(R.id.rate);
                    ImageView image = customView.findViewById(R.id.img);
                    //Glide.with(context).asBitmap().load(namesImages.get(position).image).into(image);

                    //default ratings
                    ratings[0] =(float) 3.5;
                    ratings[1] =(float) 3.5;

                    view.setText(namesImages.get(position).name);
                    mail.setText(employee.Email);

                    bar = customView.findViewById(R.id.workRating);
                    bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                ratings[0]=rating;
                        }
                    });

                    bar2 = customView.findViewById(R.id.timeRating);
                    bar2.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                            ratings[1]=rating;

                        }
                    });

                    cancelBut.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popUp.dismiss();
                                }
                    });

                    rateBut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            float rating = (ratings[0] + ratings[1])/2;
                            Ratings rate = new Ratings(
                                    GetLoggedInUserID(context),
                                    con.GetUserIDByEmployeeID(employee.ID),
                                    rating
                            );
                            try {
                                con.InsertRating(rate);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            popUp.dismiss();
                            Toast.makeText(context,"דירוג בוצע בהצלחה",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.relativeLayout.setVisibility(View.GONE);
                Toast.makeText(context,"נמחק בהצלחה",Toast.LENGTH_LONG).show();
                con.DeleteForm(jobForms.get(position).ID);

            }
        });
    }


    @Override
    public int getItemCount() {
        return jobForms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout relativeLayout;
        ImageView image;
        TextView name;
        TextView startdate;
        TextView enddate;
        TextView budget;
        TextView rooms;
        TextView city;
        TextView address;
        TextView status;
        TextView formid;
        Button rate;
        Button addtofav;
        Button delete;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout =itemView.findViewById(R.id.recycle_job_forms);
            image = itemView.findViewById(R.id.workerimage);
            name = itemView.findViewById(R.id.workername);
            startdate = itemView.findViewById(R.id.StartDate);
            enddate = itemView.findViewById(R.id.EndDate);
            budget = itemView.findViewById(R.id.Budget);
            rooms = itemView.findViewById(R.id.Rooms);
            city = itemView.findViewById(R.id.City);
            address = itemView.findViewById(R.id.Adress);
            status = itemView.findViewById(R.id.fStatus);
            rate = itemView.findViewById(R.id.ratebut);
            delete = itemView.findViewById(R.id.deletebut);
            addtofav = itemView.findViewById(R.id.addfav);
            formid = itemView.findViewById(R.id.formid);
        }
    }
}




