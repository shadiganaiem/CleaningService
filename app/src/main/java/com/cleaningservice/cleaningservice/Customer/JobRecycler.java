package com.cleaningservice.cleaningservice.Customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cleaningservice.cleaningservice.R;

import java.util.ArrayList;

import Models.JobForm;

public class JobRecycler extends RecyclerView.Adapter<JobRecycler.ViewHolder> {

    private Context context;
    private  ArrayList<JobForm> jobForms = new ArrayList<>();
    private  ArrayList<NameImage> namesImages = new ArrayList<>();

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
            holder.status.setText("פעיל");
            holder.name.setText(namesImages.get(position).name);
           // Glide.with(context).asBitmap().load(namesImages.get(position).image).into(holder.image);
        }
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


        }
    }
}




