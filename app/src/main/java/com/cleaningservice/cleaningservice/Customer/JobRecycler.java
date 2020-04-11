package com.cleaningservice.cleaningservice.Customer;

import android.content.Context;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cleaningservice.cleaningservice.ApplicationDbContext;
import com.cleaningservice.cleaningservice.R;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Models.Employee;
import Models.JobForm;

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

        holder.startdate.setText(jobForms.get(position).StartDate.toString());
        holder.enddate.setText(jobForms.get(position).EndDate.toString());
        holder.city.setText(jobForms.get(position).City);
        holder.address.setText(jobForms.get(position).Address);
        holder.budget.setText(String.valueOf(jobForms.get(position).Budget));
        holder.rooms.setText(String.valueOf(jobForms.get(position).Rooms));

        if(jobForms.get(position).StatusId==4) {
            holder.status.setText(R.string.Active);
            holder.name.setText(namesImages.get(position).name);
           // Glide.with(context).asBitmap().load(namesImages.get(position).image).into(holder.image);
        }

        try {
            con = ApplicationDbContext.getInstance(context.getApplicationContext());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Date date = new Date();
        if(jobForms.get(position).EndDate.compareTo(date) < 0){
            holder.rate.setVisibility(View.VISIBLE);

            //if no employee assigned and the form end date passed delete form
            if(namesImages.get(position).ID == 0){

            }


            holder.rate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

                    // Inflate the custom layout/view
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
                            float sum=0;
                            float rating = (ratings[0] + ratings[1])/2;
                            employee.ratings.add(rating);
                            for (Float rate : ratings){
                                sum+=rate;
                            }
                            employee.Rating = sum/employee.ratings.size();
                            con.UpdateEmployeeRating(employee.Rating);
                            con.DeleteForm(jobForms.get(position).ID);
                            popUp.dismiss();
                        }
                    });

                }
            });
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context,"dslkjfsdjkfnsdkjf",Toast.LENGTH_SHORT).show();
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




        }
    }
}




