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
import com.cleaningservice.cleaningservice.Util;
import java.util.List;
import Models.JobFormRequest;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder>  {

    private Context context;
    private List<JobFormRequest> list;


    public RequestAdapter(List<JobFormRequest> list, Context context){
        this.list  = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.job_form, parent, false);
        return new RequestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JobFormRequest request = list.get(position);
        String Title;
        Title = request.jobForm.City + " - " + request.jobForm.Address;
        String description = "";

        if(request.StatusId == Util.Statuses.ACCEPTED.value){
            description += context.getResources().getString(R.string.RequestAccepted) + "\n";
            description += context.getResources().getString(R.string.EndDateRemember) + request.jobForm.EndDate + "\n\n";
            description += context.getResources().getString(R.string.ContactCustomer) + request.jobForm.customer.Phone + "\n";
        }
        else
            description += context.getResources().getString(R.string.RequestRejected);

        holder.title.setText(Title);
        holder.description.setText(description);
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
        public TextView title, description;
        public FormAdapter.OnJobFormListiner onJobFormListiner;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
        }

    }


}

