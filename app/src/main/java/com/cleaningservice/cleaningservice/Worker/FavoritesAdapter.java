package com.cleaningservice.cleaningservice.Worker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.renderscript.Allocation;
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
import java.util.List;
import java.util.Locale;

import Authentications.Preferences;
import Models.Favorite;
import Models.JobForm;
import Models.JobFormRequest;


public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
    private List<Favorite> list;
    private Context context;
    private Handler mainhandler = new Handler();
    private Handler secondHandler = new Handler();
    private ApplicationDbContext _context = null;


    public FavoritesAdapter(List<Favorite> list, Context context){
        this.list  = list;
        this.context = context;

        try {
            _context = ApplicationDbContext.getInstance(context);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public FavoritesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.job_form, parent, false);
        return new FavoritesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesAdapter.ViewHolder holder, int position) {
        Favorite favorite = list.get(position);
        String Title;
        Title = favorite.customer.Firstname + " " + favorite.customer.Lastname;
        holder.title.setText(Title);
        String Description = "";
        if (favorite.customer.Rating != 0) {
            for (int i = 0; i < favorite.customer.Rating && i < 5; i++) {
                Description += "★";
            }
        }
        Description += "\n___________________";
        holder.description.setText(Description);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                boolean rated = false;
                int userId = Preferences.GetLoggedInUserID(context);

                if(!_context.checkIfNotRated(userId,favorite.FavoriteUserId))
                    rated = true;

                boolean finalRated = rated;
                secondHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.favoriteButtons.setVisibility(View.VISIBLE);
                        if(finalRated)
                            holder.deleteFavoriteBtn.setVisibility(View.GONE);

                        holder.image.setImageDrawable(context.getResources().getDrawable(R.mipmap.unnamed));
                        holder.image.setVisibility(View.VISIBLE);
                    }
                });

                 byte[] profileImage = _context.GetProfileImage(favorite.FavoriteUserId);
                mainhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Drawable bitmap;
                        if (profileImage != null) {
                            bitmap = new BitmapDrawable(BitmapFactory.decodeByteArray(profileImage, 0, profileImage.length));
                            holder.image.setImageDrawable(bitmap);
                        }
                    }
                });
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title, description;
        public Button deleteFavoriteBtn, favoriteRateBtn;
        public LinearLayout favoriteButtons;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            deleteFavoriteBtn = itemView.findViewById(R.id.deleteFavoriteBtn);
            favoriteRateBtn = itemView.findViewById(R.id.favoriteRateBtn);
            favoriteButtons = itemView.findViewById(R.id.favoriteButtons);

            deleteFavoriteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO UPDATE the model here
                    int position = getAdapterPosition();
                    Favorite favorite = list.get(position);

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            boolean result = _context.DeleteUserFromFavorites(favorite.ID);
                            mainhandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    CharSequence text;
                                    if (result)
                                        text = context.getResources().getString(R.string.deletedFromFavorites);
                                    else
                                        text = context.getResources().getString(R.string.ErrorHappend);

                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();

                                    itemView.setVisibility(View.GONE);
                                }
                            });
                        }
                    };
                    Thread thread = new Thread(runnable);
                    thread.start();
                }
            });
        }
    }

}
