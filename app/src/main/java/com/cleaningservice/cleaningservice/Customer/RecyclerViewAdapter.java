package com.cleaningservice.cleaningservice.Customer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cleaningservice.cleaningservice.ApplicationDbContext;
import com.cleaningservice.cleaningservice.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Models.JobForm;
import Models.Request;
import Models.User;

import static Authentications.Preferences.GetLoggedInUserID;

public class RecyclerViewAdapter extends  RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private ArrayList<Request> requests = new ArrayList<>();
    private Context context;

    public RecyclerViewAdapter(Context context, ArrayList<Request> requests){
        this.requests =  requests;
        this.context = context;
    }

    private static final String TAG = "RecyclerViewAdapter";

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_requests_list,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.name.setText(requests.get(position).FirstName+" "+ requests.get(position).LastName);
        holder.Email.setText(requests.get(position).Email);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View image;
        TextView name;
        TextView Email;
        RelativeLayout relativelayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imagehu);
            name = itemView.findViewById(R.id.title2);
            Email = itemView.findViewById(R.id.title4);
            relativelayout = itemView.findViewById(R.id.Requests);

        }
    }
}
