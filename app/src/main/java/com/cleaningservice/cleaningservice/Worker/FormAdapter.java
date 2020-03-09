package com.cleaningservice.cleaningservice.Worker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cleaningservice.cleaningservice.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.zip.Inflater;

import Models.JobForm;

public class FormAdapter extends RecyclerView.Adapter<FormAdapter.ViewHolder> {
    private List<JobForm> list;
    private Context context;

    public FormAdapter(List<JobForm> list, Context context){
        this.list  = list;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.job_form, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JobForm jobForm = list.get(position);
        //Picasso.get().load(null).into(holder.image);
        String Title;
        String Description;
        Title = jobForm.customer.Firstname + " " + jobForm.customer.Lastname;
        String roomsNumber = context.getResources().getString(R.string.RoomsNumber);
        Description = jobForm.City + " - " + jobForm.Address +"\n"+ roomsNumber + ": " + jobForm.Rooms;

        holder.title.setText(Title);
        holder.description.setText(Description);
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title;
        public TextView description;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
        }
    }
}
