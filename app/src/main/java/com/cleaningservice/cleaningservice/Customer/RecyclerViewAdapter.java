package com.cleaningservice.cleaningservice.Customer;

import android.content.Context;
import android.util.Log;
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
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import Models.Request;




public class RecyclerViewAdapter extends  RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private ArrayList<Request> requests = new ArrayList<>();
    private Context context;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        if(showShimmer){
            holder.shimmerFrameLayout.startShimmer();

        }else{
            holder.shimmerFrameLayout.stopShimmer(); // stop shimmer animation
            holder.shimmerFrameLayout.setShimmer(null); // remove shimmer

            String fullname = requests.get(position).FirstName+" "+ requests.get(position).LastName;
            holder.name.setBackground(null);
            holder.name.setText(fullname);

            holder.Email.setBackground(null);
            holder.Email.setText(requests.get(position).Email);

            Glide.with(context).asBitmap().load(requests.get(position).ImageBytes).into(holder.image);
        }


        /////////////////////////////////////////
        /*Glide.with(context).asBitmap().load(requests.get(position).ImageBytes).into(holder.image);

        String fullname = requests.get(position).FirstName+" "+ requests.get(position).LastName;
        holder.name.setText(fullname);
        holder.Email.setText(requests.get(position).Email);*/
    }

    @Override
    public int getItemCount() {
        return showShimmer ? SHIMMER_ITEM_NUM : requests.size(); // return 5 while loading , after loading return requests size
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ShimmerFrameLayout shimmerFrameLayout;
        ImageView image;
        TextView name;
        TextView Email;
        RelativeLayout relativelayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            shimmerFrameLayout =itemView.findViewById(R.id.shimmer_layout);
            image = itemView.findViewById(R.id.imagehu);
            name = itemView.findViewById(R.id.title2);
            Email = itemView.findViewById(R.id.title4);



            //////////////////////////
            /*image = itemView.findViewById(R.id.imagehu);
            name = itemView.findViewById(R.id.title2);
            Email = itemView.findViewById(R.id.title4);
            relativelayout = itemView.findViewById(R.id.Requests);*/

        }
    }
}
