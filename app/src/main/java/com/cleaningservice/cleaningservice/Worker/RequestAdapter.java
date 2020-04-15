package com.cleaningservice.cleaningservice.Worker;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cleaningservice.cleaningservice.ApplicationDbContext;
import com.cleaningservice.cleaningservice.R;
import com.cleaningservice.cleaningservice.Util;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import Authentications.Preferences;
import Models.Favorite;
import Models.JobFormRequest;
import Models.Request;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder>  {

    private Context context;
    private List<JobFormRequest> list;
    private OnJobFormRequestListener onJobFormRequestListener;
    private ApplicationDbContext _context;
    private Context getAppContext;
    private Handler mainhandler = new Handler();


    public RequestAdapter(List<JobFormRequest> list, Context context,OnJobFormRequestListener onJobFormRequestListener){
        this.list  = list;
        this.context = context;
        this.onJobFormRequestListener = onJobFormRequestListener;
        getAppContext = context;
        try {
            _context = ApplicationDbContext.getInstance(context);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

        if(request.jobForm.StatusId == Util.Statuses.CLOSED.value && request.StatusId == Util.Statuses.ACCEPTED.value) {

            description += context.getResources().getString(R.string.jobEndedDescription) + ".\n";

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    boolean added = false;
                    boolean rated = false;
                    int userId = Preferences.GetLoggedInUserID(context);
                    ArrayList<Favorite> favorites =  _context.GetUserFavoriteList(userId,Util.UserTypes.CUSTOMER);
                    int customerUserId = _context.GetUserIDByCustomerID(request.jobForm.CustomerId);

                    if(!_context.checkIfNotRated(userId,customerUserId))
                        rated = true;
                    if(!_context.checkIfNotFavorite(userId,customerUserId))
                        added = true;


                    boolean finalAdded = added;
                    boolean finalRated = rated;
                    mainhandler.post(new Runnable() {
                        @Override
                        public void run() {

                            holder.image.setImageDrawable(context.getResources().getDrawable(R.mipmap.completed));
                            holder.image.setVisibility(View.VISIBLE);
                            if(finalAdded)
                                holder.addToFavoriteBtn.setVisibility(View.INVISIBLE);
                            if(finalRated)
                                holder.rateBtn.setVisibility(View.INVISIBLE);
                            if(finalAdded && finalRated)
                                holder.endButtons.setVisibility(View.INVISIBLE);
                            else
                                holder.endButtons.setVisibility(View.VISIBLE);
                        }
                    });
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }
        else {

            if (request.StatusId == Util.Statuses.WAITING.value) {
                description += "⌛ " + context.getResources().getString(R.string.requestWatingForResponse) + "\n";
                description += context.getResources().getString(R.string.WaitingCondition);
            } else if (request.StatusId == Util.Statuses.ACCEPTED.value) {
                description += "✅ " + context.getResources().getString(R.string.RequestAccepted) + "\n";
                description += context.getResources().getString(R.string.EndDateRemember) + " " + dateFormat.format(request.jobForm.EndDate) + "\n\n";
                description += context.getResources().getString(R.string.ContactCustomer) + request.jobForm.customer.Phone + "\n";
            } else if (request.StatusId == Util.Statuses.REJECTED.value) {
                holder.description.setBackgroundColor(Color.parseColor("#a32319"));
                holder.title.setBackgroundColor(Color.parseColor("#a32319"));
                description += "❌ " + context.getResources().getString(R.string.RequestRejected) + "\n";
            }

            if(request.CreationDate != null)
                description += "______________ \n \uD83D\uDCC5 " + context.getResources().getString(R.string.RequestDate) + " " + dateFormat.format(request.CreationDate);
        }

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
        public LinearLayout endButtons;
        public Button addToFavoriteBtn;
        public Button rateBtn;
        public OnJobFormRequestListener onJobFormRequestListener;
        public ViewHolder(@NonNull View itemView,OnJobFormRequestListener onJobFormRequestListener) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            endButtons = itemView.findViewById(R.id.endButtons);
            addToFavoriteBtn = itemView.findViewById(R.id.addToFavoriteBtn);
            rateBtn = itemView.findViewById(R.id.rateBtn);

            addToFavoriteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO UPDATE the model here
                    int position = getAdapterPosition();
                    JobFormRequest request = list.get(position);
                    int jobFormId = request.JobFormId;

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            boolean result = _context.InsertFavoriteByCustomerId(Preferences.GetLoggedInUserID(context),request.jobForm.CustomerId);
                            mainhandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    CharSequence text;
                                    if (result)
                                        text = context.getResources().getString(R.string.addedToFavorite);
                                    else
                                        text = context.getResources().getString(R.string.ErrorHappend);

                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();

                                    addToFavoriteBtn.setVisibility(View.GONE);
                                }
                            });
                        }
                    };
                    Thread thread = new Thread(runnable);
                    thread.start();
                }
            });
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

