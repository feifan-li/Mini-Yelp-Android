package com.example.yelp;
//Reference:https://www.journaldev.com/23164/android-recyclerview-swipe-to-delete-undo#code
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private ArrayList<String> names;
    private ArrayList<String> data;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mIndex,mName,mDate,mEmail,mTime;
        RelativeLayout relativeLayout;

        public MyViewHolder(View itemView) {
            super(itemView);

            mIndex = itemView.findViewById(R.id.ReservationItemIndex);
            mName = itemView.findViewById(R.id.ReservationItemName);
            mDate = itemView.findViewById(R.id.ReservationItemDate);
            mEmail = itemView.findViewById(R.id.ReservationItemEmail);
            mTime = itemView.findViewById(R.id.ReservationItemTime);
        }
    }

    public RecyclerViewAdapter(ArrayList<String> names,ArrayList<String> data) {
        this.names = names;
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int i) {
        holder.mIndex.setText(""+(i+1));
        holder.mName.setText(names.get(i));
        holder.mEmail.setText(data.get(i).split("&")[0]);
        holder.mDate.setText(data.get(i).split("&")[1]);
        holder.mTime.setText(data.get(i).split("&")[2]);
    }

    @Override
    public int getItemCount() {
        return Math.min(names.size(),data.size());
    }


    public String removeItem(int position) {
        String removed_name = names.get(position);
        names.remove(position);
        data.remove(position);
        notifyItemRemoved(position);

        return removed_name;

    }

}