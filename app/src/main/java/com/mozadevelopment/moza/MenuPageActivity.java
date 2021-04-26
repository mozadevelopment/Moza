package com.mozadevelopment.moza;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mozadevelopment.moza.Adapter.ItemRecyclerViewAdapter;
import com.mozadevelopment.moza.Database.MenuHelperClass;

import java.util.ArrayList;



public class MenuPageActivity extends AppCompatActivity {

    private RecyclerView rvMenuList;
    private DatabaseReference itemsDatabaseReference;

    private ArrayList<MenuHelperClass> arrayListMenu;

    private ItemRecyclerViewAdapter recyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_page);

        rvMenuList = findViewById(R.id.rvMenuPage);
        rvMenuList.setHasFixedSize(true);
        rvMenuList.setLayoutManager(new LinearLayoutManager(this));

        itemsDatabaseReference = FirebaseDatabase.getInstance().getReference();

        arrayListMenu = new ArrayList<>();

        ClearAll();
        GetDataFromFirebase();
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

    @Override
    protected void onStart() {
        super.onStart();
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