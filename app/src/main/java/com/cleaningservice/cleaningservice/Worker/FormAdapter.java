package com.cleaningservice.cleaningservice.Worker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cleaningservice.cleaningservice.R;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.ThreadFactory;

import Models.JobForm;

public class FormAdapter extends RecyclerView.Adapter<FormAdapter.ViewHolder> {
    private List<JobForm> list;
    private List<JobForm> FilterdList;
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

    /***
     * initialize jobform item form jobforms List.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JobForm jobForm = list.get(position);
        String Title;
        String Description;
        Title = jobForm.customer.Firstname + " " + jobForm.customer.Lastname;
        String roomsNumber = context.getResources().getString(R.string.RoomsNumber);
        Description = jobForm.City + " - " + jobForm.Address + "\n" + roomsNumber + ": " + jobForm.Rooms;
        if (jobForm.customer.Rating != 0) {
            Description += "\n";
            for (int i = 0; i < jobForm.customer.Rating && i < 5; i++) {
                Description += "★";
            }
        }

        holder.title.setText(Title);
        holder.description.setText(Description);

        if (jobForm.ImageBytes != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Drawable bitmap = new BitmapDrawable(BitmapFactory.decodeByteArray(jobForm.ImageBytes, 0, jobForm.ImageBytes.length));
                    holder.image.setImageDrawable(bitmap);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * define all card view Components.
     */
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
