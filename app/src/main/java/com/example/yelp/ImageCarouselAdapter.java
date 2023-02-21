package com.example.yelp;
//Reference:https://www.youtube.com/watch?v=iJtTN5BHpzw
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageCarouselAdapter extends RecyclerView.Adapter<ImageCarouselAdapter.ViewHolder> {

    private ArrayList<String> dataList;
    private Context contect;

    public ImageCarouselAdapter(ArrayList<String> dataList, Context contect) {
        this.dataList = dataList;
        this.contect = contect;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_carousel_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageCarouselAdapter.ViewHolder holder, int position) {
        Picasso.get().load(dataList.get(position)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.carouselItemImageView);
        }
    }
}