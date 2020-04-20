package com.cleaningservice.cleaningservice.Customer;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cleaningservice.cleaningservice.ApplicationDbContext;
import com.cleaningservice.cleaningservice.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Models.Favorite;

import static Authentications.Preferences.GetLoggedInUserID;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class FavoritesRecycler extends RecyclerView.Adapter<FavoritesRecycler.ViewHolder> {
    private ArrayList<Favorite> favorites = new ArrayList<>();
    private ApplicationDbContext con = null;
    private Context context;
    private int thisCustomer;
    private PopupWindow popUp;
    private List<Integer> FormIds = new ArrayList<>();


    public FavoritesRecycler(Context context, ArrayList<Favorite> favorites){
        this.favorites =  favorites;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_recycler,parent,false);
        FavoritesRecycler.ViewHolder holder = new FavoritesRecycler.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            con = ApplicationDbContext.getInstance(context.getApplicationContext());

        } catch (SQLException e) {
            e.printStackTrace();
        }
       String fullname = favorites.get(position).employee.Firstname+" "+ favorites.get(position).employee.Lastname;
        holder.name.setText(fullname);
        holder.Email.setText( favorites.get(position).employee.Email);
        holder.Phone.setText(favorites.get(position).employee.Phone);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    con.DeleteFavorite(favorites.get(position).ID);
                    holder.relativeLayout.setVisibility(View.GONE);
                    Toast.makeText(context,"מועדף נמחק בהצלחה",Toast.LENGTH_LONG).show();
                    favorites.remove(position);
                    notifyItemRemoved(position);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });

        holder.sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button send;
                ArrayList<CheckBox> checkBoxes = new ArrayList<>();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.favorites_popup,null);
                 popUp = new PopupWindow(
                        customView,
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        true
                );
                LinearLayout linearLayout = customView.findViewById(R.id.linearLayout3);
                thisCustomer = con.GetCustomerIdByUserID(GetLoggedInUserID(context.getApplicationContext()));
                FormIds = con.GetJobFormID(thisCustomer);
                for(Integer id : FormIds) {
                    CheckBox checkBox = new CheckBox(context);
                    checkBox.setText(String.valueOf(id));
                    checkBox.setId(id);
                    checkBox.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    checkBox.setLayoutParams(lp);
                    linearLayout.addView(checkBox);
                    checkBoxes.add(checkBox);
                }
                popUp.showAtLocation(holder.relativeLayout, Gravity.CENTER,0,0);

                send = customView.findViewById(R.id.send);

                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int flag =0;
                        for(CheckBox checkBox : checkBoxes){
                            if(checkBox.isChecked()){
                                flag = 1;
                                con.InsertCustomerJobFormRequest(thisCustomer , checkBox.getId(), favorites.get(position).employee.ID);
                            }
                        }
                        if (flag == 0){
                            Toast.makeText(context,"נא לבחור לפחות טופס אחד",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(context,"הבקשה נשלחה בהצלחה",Toast.LENGTH_SHORT).show();
                            popUp.dismiss();
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout relativeLayout;
       // ImageView image;
        TextView name;
        TextView Email;
        TextView Phone;
        //TextView rating;
        Button delete;
        Button sendRequest;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            relativeLayout =itemView.findViewById(R.id.relativeFavoritesLayout);
         //   image = itemView.findViewById(R.id.imagehu);
            name = itemView.findViewById(R.id.fullname);
            Email = itemView.findViewById(R.id.mail);
            Phone = itemView.findViewById(R.id.phone);
            sendRequest = itemView.findViewById(R.id.sendRequestBtn);
            delete = itemView.findViewById(R.id.deletebut);
           // rating = itemView.findViewById(R.id.rating);

        }
    }
}
