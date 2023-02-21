package com.example.yelp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ResultsRecyclerAdapter extends RecyclerView.Adapter<ResultsRecyclerAdapter.ItemViewHolder>{
    private ArrayList<JSONObject> resultsList;
    private Context context;

    public ResultsRecyclerAdapter(ArrayList<JSONObject> resultsList, Context context) {
        this.resultsList = resultsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ResultsRecyclerAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_item_design,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultsRecyclerAdapter.ItemViewHolder holder, int position) {
        holder.indexTextView.setText(""+(position+1));
        try {
            String image_url = resultsList.get(position).getString("image_url");
            String id = resultsList.get(position).getString("id");
            String name = resultsList.get(position).getString("name");
            Picasso.get().load(image_url).into(holder.imageView);
            holder.businessTitleTextView.setText(name);
            holder.ratingTextView.setText(""+resultsList.get(position).getString("rating"));
            holder.distanceTextView.setText(""+resultsList.get(position).getString("distance"));

            holder.itemCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,DetailsActivity.class);
                    intent.putExtra("id",id);
                    intent.putExtra("name",name);
                    context.startActivity(intent);
//                    Toast.makeText(context, name,Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }
    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private CardView itemCardView;
        private TextView indexTextView, businessTitleTextView,ratingTextView,distanceTextView;
        private ImageView imageView;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCardView = itemView.findViewById(R.id.ItemCardView);
            indexTextView = itemView.findViewById(R.id.indexTextView);
            businessTitleTextView = itemView.findViewById(R.id.businessTitleTextView);
            ratingTextView = itemView.findViewById(R.id.RatingTextView);
            distanceTextView = itemView.findViewById(R.id.distanceTextView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
