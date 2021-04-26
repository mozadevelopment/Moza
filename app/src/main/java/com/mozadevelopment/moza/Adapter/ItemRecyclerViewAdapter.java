package com.mozadevelopment.moza.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import android.content.Context;
import com.mozadevelopment.moza.Database.MenuHelperClass;
import com.mozadevelopment.moza.R;

import java.util.ArrayList;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {

    public Context mContext;
    private ArrayList<MenuHelperClass> itemList;

    public ItemRecyclerViewAdapter(Context mContext, ArrayList<MenuHelperClass> itemList) {
        this.mContext = mContext;
        this.itemList = itemList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImage;
        TextView itemName, itemDescription, itemPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemDescription = itemView.findViewById(R.id.itemDescription);
            itemPrice = itemView.findViewById(R.id.itemPrice);
        }
    }

    @NonNull
    @Override
    public ItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemName.setText(itemList.get(position).getName());
        holder.itemDescription.setText(itemList.get(position).getDescription());
        holder.itemPrice.setText("$" + itemList.get(position).getPrice());

        Glide.with(mContext)
                .load(itemList.get(position).getImageUrl())
                .into(holder.itemImage);
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
