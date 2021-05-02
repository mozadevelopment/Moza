package com.mozadevelopment.moza;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mozadevelopment.moza.Database.MenuHelperClass;

import java.util.ArrayList;

public class MenuPageActivity extends AppCompatActivity {

    private RecyclerView rvMenuList;
    private DatabaseReference itemsDatabaseReference;
    private Button openCartButton;
    private ArrayList<MenuHelperClass> arrayListMenu;
    private String amountAdded;
    private ItemRecyclerViewAdapter recyclerAdapter;
    int amountAddedInt, itemsInCart = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_page);

        rvMenuList = findViewById(R.id.rvMenuPage);
        rvMenuList.setHasFixedSize(true);
        rvMenuList.setLayoutManager(new LinearLayoutManager(this));

        itemsDatabaseReference = FirebaseDatabase.getInstance().getReference();

        arrayListMenu = new ArrayList<>();

        //Cart button visibility

        amountAdded = getIntent().getStringExtra("amountItemsAdded");

        try {
            amountAddedInt = Integer.parseInt(amountAdded);
            itemsInCart = amountAddedInt;
        } catch(NumberFormatException nfe){
            System.out.println("Could not parse string " + nfe);
        }

        ClearAll();
        GetDataFromFirebase();

        openCartButton = findViewById(R.id.buttonOpenCart);
        if (itemsInCart > 0) {
            openCartButton.setVisibility(View.VISIBLE);
        } else {
            openCartButton.setVisibility(View.INVISIBLE);
        }

        openCartButton.setOnClickListener(view -> startActivity(new Intent(MenuPageActivity.this, CartActivity.class)));
    }

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
            CardView itemCardView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                itemImage = itemView.findViewById(R.id.itemImage);
                itemName = itemView.findViewById(R.id.itemName);
                itemDescription = itemView.findViewById(R.id.itemDescription);
                itemPrice = itemView.findViewById(R.id.itemPrice);
                itemCardView = itemView.findViewById(R.id.itemCardView);
            }
        }

        @NonNull
        @Override
        public ItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_layout, parent, false);
            return new ItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.itemName.setText(itemList.get(position).getName());
            holder.itemDescription.setText(itemList.get(position).getDescription());
            holder.itemPrice.setText("$" + itemList.get(position).getPrice());

            Glide.with(mContext)
                    .load(itemList.get(position).getImageUrl())
                    .into(holder.itemImage);
            holder.itemCardView.setOnClickListener(v -> {
                Intent intent = new Intent(MenuPageActivity.this, ItemDetailsActivity.class);
                intent.putExtra("itemUrl", itemList.get(position).getImageUrl());
                intent.putExtra("itemName", itemList.get(position).getName());
                intent.putExtra("itemDescription", itemList.get(position).getDescription());
                intent.putExtra("itemPrice", itemList.get(position).getPrice());
                intent.putExtra("itemId", itemList.get(position).getItemId());
                intent.putExtra("itemsInCart", itemsInCart);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }
    }

    private void ClearAll(){
        if (arrayListMenu != null) {
            arrayListMenu.clear();

            if (recyclerAdapter != null) {
                recyclerAdapter.notifyDataSetChanged();
            }

        } else {
            arrayListMenu = new ArrayList<>();
        }
    }

    private void GetDataFromFirebase() {

        Query query = itemsDatabaseReference.child("Menu").child("Items");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ClearAll();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MenuHelperClass items = new MenuHelperClass();

                    items.setImageUrl(dataSnapshot.child("imageURL").getValue().toString());
                    items.setName(dataSnapshot.child("name").getValue().toString());
                    items.setDescription(dataSnapshot.child("description").getValue().toString());
                    items.setPrice(dataSnapshot.child("price").getValue().toString());
                    items.setItemId(dataSnapshot.child("itemId").getValue().toString());

                    arrayListMenu.add(items);
                }

                recyclerAdapter = new ItemRecyclerViewAdapter(getApplicationContext(), arrayListMenu);
                rvMenuList.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.delete_cart_list_alert);
        builder.setMessage(R.string.confirm_delete_cart_alert);

        builder.setPositiveButton(R.string.ok_alert_button, (dialog, which) -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Cart List").child(userId);
            ref.removeValue();
            super.onBackPressed();
        });

        builder.setNegativeButton(R.string.no_alert_button, (dialog, which) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }
}