package com.mozadevelopment.moza;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mozadevelopment.moza.Database.CartHelperClass;
import com.mozadevelopment.moza.Database.OrderHelperClass;

import java.util.ArrayList;

public class AdminHomeFragment extends Fragment {

    private ArrayList<OrderHelperClass> arrayListMenu;
    private OrdersRecyclerViewAdapter recyclerAdapterOrders;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_admin_home, container, false);

        recyclerView = rootView.findViewById(R.id.ordersRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        arrayListMenu = new ArrayList<>();

        getDataFromFirebase();

        return rootView;

    }

    private void getDataFromFirebase() {

        Query query = FirebaseDatabase.getInstance().getReference().child("Orders");

        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot usersIdsSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot timestampIdsSnapshot : usersIdsSnapshot.getChildren()) {

                        OrderHelperClass items = new OrderHelperClass();

                        items.setTimestamp(timestampIdsSnapshot.child("timestamp").getValue().toString());
                        items.setAddress(timestampIdsSnapshot.child("address").getValue().toString());
                        items.setAnnotation(timestampIdsSnapshot.child("annotations").getValue().toString());
                        items.setPayment(timestampIdsSnapshot.child("payment").getValue().toString());
                        items.setPrice(timestampIdsSnapshot.child("orderTotalPrice").getValue().toString());
                        items.setStatus(timestampIdsSnapshot.child("orderStatus").getValue().toString());

                        arrayListMenu.add(items);
                    }
                }

                recyclerAdapterOrders = new AdminHomeFragment.OrdersRecyclerViewAdapter(getContext(), arrayListMenu);
                recyclerView.setAdapter(recyclerAdapterOrders);
                recyclerAdapterOrders.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class OrdersRecyclerViewAdapter extends RecyclerView.Adapter<OrdersRecyclerViewAdapter.ViewHolder> {

        public Context mContext;
        private ArrayList<OrderHelperClass> itemList;

        public OrdersRecyclerViewAdapter(Context context, ArrayList<OrderHelperClass> itemList) {
            this.mContext = context;
            this.itemList = itemList;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView tvOrderTime, tvOrderAddress, tvOrderPayment, tvOrderAnnotation, tvOrderPrice, tvOrderStatus;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvOrderTime = itemView.findViewById(R.id.orderTimestamp);
                tvOrderAddress = itemView.findViewById(R.id.orderAddress);
                tvOrderAnnotation = itemView.findViewById(R.id.orderAnnotations);
                tvOrderPayment = itemView.findViewById(R.id.orderPayment);
                tvOrderPrice = itemView.findViewById(R.id.orderPrice);
                tvOrderStatus= itemView.findViewById(R.id.orderStatus);
            }
        }

        @NonNull
        @Override
        public OrdersRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout, parent, false);
            return new OrdersRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrdersRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.tvOrderTime.setText(itemList.get(position).getTimestamp());
            holder.tvOrderAddress.setText(itemList.get(position).getAddress());
            holder.tvOrderAnnotation.setText(itemList.get(position).getAnnotation());
            holder.tvOrderPayment.setText(itemList.get(position).getPayment());
            holder.tvOrderPrice.setText("$" + itemList.get(position).getPrice());
            holder.tvOrderStatus.setText(itemList.get(position).getStatus());
        }

        @Override
        public int getItemCount() {

            return itemList.size();
        }
    }
}
