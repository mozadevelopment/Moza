package com.mozadevelopment.moza.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mozadevelopment.moza.ItemClickListener;
import com.mozadevelopment.moza.R;

public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public ItemClickListener listener;
    public TextView tvItemName, tvItemDescription, tvItemPrice;
    public ImageView ivItemImage;

    public ItemViewHolder(View itemView){
        super(itemView);

        ivItemImage = itemView.findViewById(R.id.itemImage);
        tvItemName = itemView.findViewById(R.id.itemName);
        tvItemDescription = itemView.findViewById(R.id.itemDescription);
        tvItemPrice = itemView.findViewById(R.id.itemPrice);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(), false);
    }
}