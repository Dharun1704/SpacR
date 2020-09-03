package com.example.spacr;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacr.Models.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private Context context;
    private ArrayList<Item> searchItems;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public Adapter(Context context, ArrayList<Item> searchItems) {
        this.context = context;
        this.searchItems = searchItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.search_layout, parent, false);
        return new ViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = searchItems.get(position);

        String media = item.getData().get(0).getMedia_type();
        if (media.equals("image") && item.getLinks() != null) {
            holder.playButton.setVisibility(View.GONE);
            Picasso
                    .get()
                    .load(item.getLinks().get(0).getHref())
                    .fit()
                    .centerCrop()
                    .into(holder.searchImage);
        }
        if (media.equals("video") && item.getLinks() != null) {
            holder.playButton.setVisibility(View.VISIBLE);
                Picasso
                        .get()
                        .load(item.getLinks().get(0).getHref())
                        .fit()
                        .centerCrop()
                        .into(holder.searchImage);
        }

    }

    @Override
    public int getItemCount() {
        return searchItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView searchImage, playButton;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            searchImage = itemView.findViewById(R.id.search_image);
            playButton = itemView.findViewById(R.id.video_playImg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            listener.onItemClick(position);
                    }
                }
            });
        }
    }
}
