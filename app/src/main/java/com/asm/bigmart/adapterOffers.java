package com.asm.bigmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class adapterOffers extends RecyclerView.Adapter<adapterOffers.ViewHolder> {

    private List<String> offers = new ArrayList<>();
    private Context mContext;

    public adapterOffers(Context mContext, List<String> offers ) {
        this.offers = offers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemoffer,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int i = mContext.getResources().getIdentifier(offers.get(position), "drawable", mContext.getPackageName());
        holder.offerImage.setImageResource(i); //setText(offers.get(position));
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView offerImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            offerImage = itemView.findViewById(R.id.imgOffer);
        }
    }
}
