package com.cleaningservice.cleaningservice.Worker;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cleaningservice.cleaningservice.R;
import com.cleaningservice.cleaningservice.Util;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import Models.JobFormRequest;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder>  {

    private Context context;
    private List<JobFormRequest> list;
    private OnJobFormRequestListener onJobFormRequestListener;

    public RequestAdapter(List<JobFormRequest> list, Context context,OnJobFormRequestListener onJobFormRequestListener){
        this.list  = list;
        this.context = context;
        this.onJobFormRequestListener = onJobFormRequestListener;
    }

    @NonNull
    @Override
    public RequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.job_form, parent, false);
        return new RequestAdapter.ViewHolder(view,onJobFormRequestListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        JobFormRequest request = list.get(position);
        String Title;
        Title = request.jobForm.City + " - " + request.jobForm.Address;
        String description = "";

        if(request.StatusId == Util.Statuses.WAITING.value){
            description += "⌛ "+ context.getResources().getString(R.string.requestWatingForResponse) + "\n";
            description += context.getResources().getString(R.string.WaitingCondition);
        }
        else if(request.StatusId == Util.Statuses.ACCEPTED.value){
            description += "✅ " + context.getResources().getString(R.string.RequestAccepted) + "\n";
            description += context.getResources().getString(R.string.EndDateRemember) + " " + dateFormat.format(request.jobForm.EndDate) + "\n\n";
            description += context.getResources().getString(R.string.ContactCustomer) + request.jobForm.customer.Phone + "\n";
        }
        else if (request.StatusId == Util.Statuses.REJECTED.value){
            holder.description.setBackgroundColor(Color.parseColor("#a32319"));
            holder.title.setBackgroundColor(Color.parseColor("#a32319"));
            description += "❌ "+context.getResources().getString(R.string.RequestRejected) + "\n";
        }

        if(request.CreationDate != null)
            description += "______________ \n \uD83D\uDCC5 " + context.getResources().getString(R.string.RequestDate) + " " + dateFormat.format(request.CreationDate);

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
    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        public ImageView image;
        public TextView title, description;
        public OnJobFormRequestListener onJobFormRequestListener;
        public ViewHolder(@NonNull View itemView,OnJobFormRequestListener onJobFormRequestListener) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            this.onJobFormRequestListener = onJobFormRequestListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onJobFormRequestListener.onJobFormRequestClick(getAdapterPosition());
        }
    }

    /**
     * on Click Listener
     */
    public interface OnJobFormRequestListener{
        void onJobFormRequestClick(int position);
    }

}

