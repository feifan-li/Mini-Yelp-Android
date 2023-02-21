package com.example.yelp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReviewsRecyclerAdapter extends RecyclerView.Adapter<ReviewsRecyclerAdapter.ItemViewHolder> {
    private ArrayList<String> names;
    private ArrayList<Integer> ratings;
    private ArrayList<String> comments;
    private ArrayList<String> dates;

    private Context context;

    public ReviewsRecyclerAdapter(ArrayList<String> names, ArrayList<Integer> ratings, ArrayList<String> comments, ArrayList<String> dates, Context context) {
        this.names = names;
        this.ratings = ratings;
        this.comments = comments;
        this.dates = dates;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsRecyclerAdapter.ItemViewHolder holder, int i) {
        holder.nameTextView.setText(names.get(i));
        holder.ratingTextView.setText("Rating : "+ratings.get(i)+"/5");
        holder.commentTextView.setText(comments.get(i));
        holder.dateTextView.setText(dates.get(i));
        holder.reviewItemDivider.setVisibility(View.VISIBLE);
        if(getItemCount()-1==i){
            holder.reviewItemDivider.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return ratings.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private ConstraintLayout ReviewItem;
        private TextView nameTextView,ratingTextView,commentTextView,dateTextView;
        private View reviewItemDivider;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ReviewItem = itemView.findViewById(R.id.ReviewItem);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            reviewItemDivider = itemView.findViewById(R.id.reviewItemDivider);
        }
    }
}
