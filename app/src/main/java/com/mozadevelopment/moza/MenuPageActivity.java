package com.mozadevelopment.moza;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mozadevelopment.moza.Database.MenuHelperClass;
import com.mozadevelopment.moza.ViewHolder.ItemViewHolder;
import com.squareup.picasso.Picasso;

public class MenuPageActivity extends AppCompatActivity {

    private RecyclerView rvMenuList;
    private DatabaseReference itemsDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_page);

        rvMenuList = findViewById(R.id.rvMenuPage);
        rvMenuList.setHasFixedSize(true);
        rvMenuList.setLayoutManager(new LinearLayoutManager(this));

        itemsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Items");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<MenuHelperClass> options =
                new FirebaseRecyclerOptions.Builder<MenuHelperClass>()
                .setQuery(itemsDatabaseReference, MenuHelperClass.class)
                .build();

        FirebaseRecyclerAdapter<MenuHelperClass, ItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<MenuHelperClass, ItemViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull MenuHelperClass model) {
                        holder.tvItemName.setText(model.getName());
                        holder.tvItemDescription.setText(model.getDescription());
                        holder.tvItemPrice.setText("$" + model.getPrice());
                        Picasso.get().load(model.getImage()).into(holder.ivItemImage);
                    }

                    @NonNull
                    @Override
                    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_layout, parent, false);
                        ItemViewHolder holder = new ItemViewHolder(view);
                        return holder;
                    }
                };

        rvMenuList.setAdapter(adapter);
        adapter.startListening();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}