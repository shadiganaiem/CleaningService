package com.cleaningservice.cleaningservice.Customer;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cleaningservice.cleaningservice.ApplicationDbContext;
import com.cleaningservice.cleaningservice.R;
import com.cleaningservice.cleaningservice.Util;

import java.sql.SQLException;
import java.util.ArrayList;

import Models.Request;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private ArrayList<Request> requests = new ArrayList<>();
    private ApplicationDbContext con = null;
    private Context context;
    private Handler mainHandler = new Handler();
    String Rating = "";
    boolean showShimmer = true;
    int SHIMMER_ITEM_NUM = 5; //number of items shown while loading

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)  {
        Log.d(TAG, "onBindViewHolder: called.");


           // holder.shimmerFrameLayout.startShimmer();

            //holder.shimmerFrameLayout.stopShimmer(); // stop shimmer animation
            //holder.shimmerFrameLayout.setShimmer(null); // remove shimmer



            new Thread(){
                @Override
                public void run() {
                    super.run();




            if (requests.get(position).Rating != 0) {
                for (int i = 0; i < requests.get(position).Rating && i < 5; i++) {
                    Rating += "★";
                }
            }
            mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
            holder.rating.setBackground(null);
            holder.rating.setText(Rating);

            String fullname = requests.get(position).FirstName+" "+ requests.get(position).LastName;
            holder.name.setBackground(null);
            holder.name.setText(fullname);
            holder.formId.setText(String.valueOf(requests.get(position).JobFormID));
            holder.Email.setBackground(null);
            holder.Email.setText(requests.get(position).Email);

            Glide.with(context).asBitmap().load(requests.get(position).ImageBytes).into(holder.image);
                }
            });
            try {
                con = ApplicationDbContext.getInstance(context.getApplicationContext());
            } catch (SQLException e) {
                e.printStackTrace();
            }
                }
            }.start();

           holder.confirm.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   requests.get(position).Status_id = Util.Statuses.ACCEPTED.value;
                   holder.shimmerFrameLayout.setVisibility(View.INVISIBLE);
                   Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show();
                   con.UpdateRequestStatus(requests.get(position).EmployeeId, requests.get(position).JobFormID, Util.Statuses.ACCEPTED.value);
                   con.UpdateFormStatus(requests.get(position).JobFormID, Util.Statuses.NOTAVAILABLE.value);
               }
           });

           holder.reject.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   requests.get(position).Status_id= Util.Statuses.REJECTED.value;
                   holder.shimmerFrameLayout.setVisibility(View.INVISIBLE);
                   Toast.makeText(context,"Request Rejected",Toast.LENGTH_SHORT).show();
                   con.UpdateRequestStatus(requests.get(position).EmployeeId,requests.get(position).JobFormID, Util.Statuses.REJECTED.value);
               }
           });



    }

    @Override
    public int getItemCount() {
        return requests.size(); // return 5 while loading , after loading return requests size
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout shimmerFrameLayout;
        ImageView image;
        TextView name;
        TextView Email;
        TextView rating;
        Button confirm;
        Button reject;
        TextView formId;

       // RelativeLayout relativelayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            shimmerFrameLayout =itemView.findViewById(R.id.Requests);
            image = itemView.findViewById(R.id.imagehu);
            name = itemView.findViewById(R.id.title2);
            Email = itemView.findViewById(R.id.title4);
            confirm = itemView.findViewById(R.id.confirm);
            reject = itemView.findViewById(R.id.reject);
            rating = itemView.findViewById(R.id.rating);
            formId = itemView.findViewById(R.id.formid);


            //////////////////////////
            /*image = itemView.findViewById(R.id.imagehu);
            name = itemView.findViewById(R.id.title2);
            Email = itemView.findViewById(R.id.title4);
            relativelayout = itemView.findViewById(R.id.Requests);*/

        }
    }

}
