package com.mozadevelopment.moza;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mozadevelopment.moza.Database.CartHelperClass;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button checkoutButton, addMoreButton;
    private TextView textViewTotalAmount;
    private ArrayList<CartHelperClass> arrayListMenu;
    private String userId, timestamp, totalPriceString;
    private int totalPrice = 0;
    private CartRecyclerViewAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recyclerViewCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        arrayListMenu = new ArrayList<>();

        checkoutButton = findViewById(R.id.buttonCheckout);
        addMoreButton = findViewById(R.id.buttonGoBackToMenu);
        textViewTotalAmount = findViewById(R.id.textViewOrderAmount);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        getDataFromFirebase();

        addMoreButton.setOnClickListener(view -> startActivity(new Intent(CartActivity.this, MenuPageActivity.class)));
        checkoutButton.setOnClickListener(view -> {
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            intent.putExtra("totalPrice", totalPriceString);
            startActivity(intent);
        });
    }

    private void getDataFromFirebase() {

        Query query = FirebaseDatabase.getInstance().getReference().child("Cart List").child(userId);

        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CartHelperClass items = new CartHelperClass();

                    items.setItemName(dataSnapshot.child("name").getValue().toString());
                    items.setItemAmount(dataSnapshot.child("amount").getValue().toString());
                    items.setItemPrice(dataSnapshot.child("price").getValue().toString());
                    items.setItemDescription(dataSnapshot.child("description").getValue().toString());
                    items.setItemId(dataSnapshot.child("itemId").getValue().toString());
                    items.setTimestamp(dataSnapshot.child("timestamp").getValue().toString());

                    arrayListMenu.add(items);
                }

                recyclerAdapter = new CartActivity.CartRecyclerViewAdapter(getApplicationContext(), arrayListMenu);
                recyclerView.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class CartRecyclerViewAdapter extends RecyclerView.Adapter<CartRecyclerViewAdapter.ViewHolder> {

        public Context mContext;
        private ArrayList<CartHelperClass> itemList;

        public CartRecyclerViewAdapter(Context context, ArrayList<CartHelperClass> itemList) {
            this.mContext = context;
            this.itemList = itemList;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView tvItemName, tvItemDescription, tvItemPrice, tvItemAmount;
            public CardView itemCardView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvItemName = itemView.findViewById(R.id.cartItemName);
                tvItemDescription = itemView.findViewById(R.id.cartItemDescription);
                tvItemPrice = itemView.findViewById(R.id.cartItemPrice);
                tvItemAmount = itemView.findViewById(R.id.cartItemAmount);
                itemCardView = itemView.findViewById(R.id.cartItemCardView);
            }
        }

        @NonNull
        @Override
        public CartRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
            return new CartRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvItemName.setText(itemList.get(position).getItemName());
            holder.tvItemAmount.setText(itemList.get(position).getItemAmount());
            holder.tvItemDescription.setText(itemList.get(position).getItemDescription());

            String itemPriceString = String.valueOf(itemList.get(position).getItemPrice());
            holder.tvItemPrice.setText("$" + itemPriceString);

            //Calculate total price

            totalPrice = totalPrice + (Integer.parseInt(itemList.get(position).getItemPrice()));
            totalPriceString = String.valueOf(totalPrice);
            textViewTotalAmount.setText("$" + totalPriceString);

            //Edit item in cart
            holder.itemCardView.setOnClickListener(v -> {

                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                builder.setCancelable(false);
                builder.setTitle(R.string.edit_cart);
                builder.setMessage(R.string.confirm_delete_item_cart_alert);

                builder.setPositiveButton(R.string.delete_item, (dialog, which) -> {
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    timestamp = itemList.get(position).getTimestamp();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Cart List").child(userId).child(timestamp);
                    ref.removeValue()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CartActivity.this, R.string.item_removed, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(CartActivity.this, MenuPageActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            });
                });

                builder.setNegativeButton(R.string.no_alert_button, (dialog, which) -> dialog.cancel());

                AlertDialog alert = builder.create();
                alert.show();
            });
        }

        @Override
        public int getItemCount() {

            return itemList.size();
        }
    }
}