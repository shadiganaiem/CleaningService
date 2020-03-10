package com.cleaningservice.cleaningservice.Worker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cleaningservice.cleaningservice.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;
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
        Runnable task = () -> {
        JobForm jobForm = list.get(position);
        String Title;
        String Description;
        Title = jobForm.customer.Firstname + " " + jobForm.customer.Lastname;
        String roomsNumber = context.getResources().getString(R.string.RoomsNumber);
        Description = jobForm.City + " - " + jobForm.Address +"\n"+ roomsNumber + ": " + jobForm.Rooms;
        if(jobForm.customer.Rating != 0){
            Description +="\n";
            for (int i =0 ;i<jobForm.customer.Rating && i<5 ;i++){
                Description += "★";
            }
        }

        holder.title.setText(Title);
        holder.description.setText(Description);

                if (jobForm.bitmapString != null && jobForm.bitmapString != "") {

                    byte[] encodeByte = Base64.decode(jobForm.bitmapString, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                    holder.image.setImageBitmap(bitmap);
                    //Picasso.get().load(bitmap).into(holder.image);
                }
            };
            task.run();
            Thread thread = new Thread(task);
            thread.start();
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

    /*
    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    FilterdList = list;
                } else {
                    List<JobForm> filteredList = new ArrayList<>();
                    for (JobForm row : list) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getPhone().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<Contact>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

     */

    public interface ContactsAdapterListener {
        void onContactSelected(JobForm jobForm);
    }
}
