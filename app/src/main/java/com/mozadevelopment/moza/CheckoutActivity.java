package com.mozadevelopment.moza;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mozadevelopment.moza.Database.CartHelperClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CheckoutActivity extends AppCompatActivity {

    String userId, totalPrice;
    private ArrayList<CartHelperClass> arrayListMenu;
    private CheckoutRecyclerViewAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TextView textViewTotalAmount;

    private Button finishOrder;
    private EditText etCheckoutAnnotation, etCheckoutAddress;
    private String selectedPaymentOption, checkoutAddress, checkoutAnnotation;
    HashMap<String, String> mapItems = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        recyclerView = findViewById(R.id.recyclerViewCheckout);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayListMenu = new ArrayList<>();

        etCheckoutAnnotation = findViewById(R.id.editTextCheckoutAnnotation);
        etCheckoutAddress = findViewById(R.id.editTextAddress);

        textViewTotalAmount = findViewById(R.id.textViewCheckoutAmount);
        totalPrice = getIntent().getStringExtra("totalPrice");
        textViewTotalAmount.setText("$" + totalPrice);

        finishOrder = findViewById(R.id.buttonFinishOrder);
        finishOrder.setOnClickListener(v -> {

            getCheckoutOptions();

            if (!validateAddress() | !validatePaymentOption()) {
                return;
            } else {
                addOrderToFirebase();
                dropCartListFromFirebase();

                Intent intent = new Intent(CheckoutActivity.this, MenuPageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), R.string.orderHasBeenSentToast, Toast.LENGTH_SHORT).show();
            }
        });

        getDataFromFirebase();
    }

    private void getCheckoutOptions() {
        //Traer opcion de pago
        RadioGroup radioGroup = findViewById(R.id.paymentButtons);
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = radioGroup.findViewById(radioButtonId);

        if (!validatePaymentOption()) {
            return;
        } else {
            selectedPaymentOption = radioButton.getText().toString();
        }

        //Direccion y anotaciones para el restaurante

        checkoutAddress = etCheckoutAddress.getText().toString().trim();
        checkoutAnnotation = etCheckoutAnnotation.getText().toString().trim();
    }

    private void dropCartListFromFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Cart List").child(userId);
        ref.removeValue();
    }

    private void getDataFromFirebase() {

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
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

                recyclerAdapter = new CheckoutRecyclerViewAdapter(getApplicationContext(), arrayListMenu);
                recyclerView.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addOrderToFirebase() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Orders");
        SimpleDateFormat simpleDateFormat;

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String timestamp = simpleDateFormat.format(new Date());
        String orderStatus = getString(R.string.order_status);

        //Datos de los items
        Query query = FirebaseDatabase.getInstance().getReference().child("Cart List").child(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    mapItems.put("name", dataSnapshot.child("name").getValue().toString());
                    mapItems.put("amount", dataSnapshot.child("amount").getValue().toString());
                    mapItems.put("price", dataSnapshot.child("price").getValue().toString());
                    mapItems.put("itemId", dataSnapshot.child("itemId").getValue().toString());
                    mapItems.put("annotation", dataSnapshot.child("annotation").getValue().toString());

                    String itemTimestamp = dataSnapshot.child("timestamp").getValue().toString();
                    mapItems.put("timestamp", itemTimestamp);
                    ref.child(userId).child(timestamp).child("Items").child(itemTimestamp).setValue(mapItems);
                }
            }

            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }
        });

        HashMap<String, String> map = new HashMap<>();
        map.put("userId", userId);
        map.put("address", checkoutAddress);
        map.put("orderTotalPrice", totalPrice);
        map.put("annotations", checkoutAnnotation);
        map.put("payment", selectedPaymentOption);
        map.put("timestamp", timestamp);
        map.put("orderStatus", orderStatus);
        ref.child(userId).child(timestamp).setValue(map);
    }

    //Agregar items del pedido al Recycler View

    public class CheckoutRecyclerViewAdapter extends RecyclerView.Adapter<CheckoutRecyclerViewAdapter.ViewHolder> {

        public Context mContext;
        private ArrayList<CartHelperClass> itemList;

        public CheckoutRecyclerViewAdapter(Context context, ArrayList<CartHelperClass> itemList) {
            this.mContext = context;
            this.itemList = itemList;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView tvItemName, tvItemDescription, tvItemPrice, tvItemAmount;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvItemName = itemView.findViewById(R.id.cartItemName);
                tvItemDescription = itemView.findViewById(R.id.cartItemDescription);
                tvItemPrice = itemView.findViewById(R.id.cartItemPrice);
                tvItemAmount = itemView.findViewById(R.id.cartItemAmount);
            }
        }

        @NonNull
        @Override
        public CheckoutRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
            return new CheckoutRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvItemName.setText(itemList.get(position).getItemName());
            holder.tvItemAmount.setText(itemList.get(position).getItemAmount());
            holder.tvItemDescription.setText(itemList.get(position).getItemDescription());
            String itemPriceString = String.valueOf(itemList.get(position).getItemPrice());
            holder.tvItemPrice.setText("$" + itemPriceString);
        }

        @Override
        public int getItemCount() {

            return itemList.size();
        }
    }

    //Validar datos del checkout

    private boolean validateAddress() {
        String val = etCheckoutAddress.getText().toString().trim();
        String addressNeededToast = getString(R.string.addressNeededToast);

        if (val.isEmpty()){
            etCheckoutAddress.setError(addressNeededToast);
            return false;
        } else {
            etCheckoutAddress.setError(null);
            return true;
        }
    }

    private boolean validatePaymentOption() {
        RadioGroup radioGroup = findViewById(R.id.paymentButtons);
        String paymentNeededToast = getString(R.string.paymentNeededToast);

        if (radioGroup.getCheckedRadioButtonId() == -1){
            Toast.makeText(getApplicationContext(), paymentNeededToast, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }




}

