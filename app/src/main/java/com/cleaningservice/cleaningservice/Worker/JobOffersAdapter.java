package com.cleaningservice.cleaningservice.Worker;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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

import com.cleaningservice.cleaningservice.ApplicationDbContext;
import com.cleaningservice.cleaningservice.R;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import Authentications.Preferences;
import Models.Favorite;
import Models.JobForm;
import Models.JobFormRequest;
import Models.Request;

public class JobOffersAdapter extends RecyclerView.Adapter<JobOffersAdapter.ViewHolder> {
    private List<JobFormRequest> list;
    private Context context;
    private OnJobProposalListener onJobProposalListener;
    private ApplicationDbContext _context = null;
    PopupWindow popUp;

    public JobOffersAdapter(List<JobFormRequest> list, Context context, OnJobProposalListener onJobProposalListener){
        this.list  = list;
        this.context = context;
        this.onJobProposalListener = onJobProposalListener;
        try {
            _context = ApplicationDbContext.getInstance(context);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.job_form, parent, false);

        return new ViewHolder(view,onJobProposalListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        JobFormRequest request = list.get(position);
        String Title;
        String Description;
        Title = request.jobForm.customer.Firstname + " " + request.jobForm.customer.Lastname;
        Description = request.jobForm.City + " - " + request.jobForm.Address;
        if (request.jobForm.customer.Rating != 0) {
            Description += "\n";
            for (int i = 0; i < request.jobForm.customer.Rating && i < 5; i++) {
                Description += "★";
            }
        }

        Description += "\n___________________\n \uD83D\uDCC5 ";
        Description += dateFormat.format(request.jobForm.EndDate)  + " - " +  dateFormat.format(request.jobForm.StartDate);

        holder.title.setText(Title);
        holder.description.setText(Description);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                holder.image.setImageDrawable(context.getResources().getDrawable(R.mipmap.job_offer));
                holder.image.setVisibility(View.VISIBLE);
            }
        });
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
        public LinearLayout endButtons;
        public OnJobProposalListener onJobProposalListener;

        public ViewHolder(@NonNull View itemView, OnJobProposalListener onJobProposalListener) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            endButtons = itemView.findViewById(R.id.endButtons);

            this.onJobProposalListener = onJobProposalListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onJobProposalListener.onJobProposalClick(getAdapterPosition());
        }
    }

    /**
     * on Click Listener
     */
    public interface OnJobProposalListener{
        void onJobProposalClick(int position);
    }
}
