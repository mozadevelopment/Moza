package com.mozadevelopment.moza;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ItemDetailsActivity extends AppCompatActivity {

    ImageView itemDetailsImage;
    TextView itemDetailsName, itemDetailsDescription, itemDetailsPrice;
    Button addToCartButton, openCartButton;
    ElegantNumberButton amountButton;
    EditText etItemDetailsAnnotation;
    String itemImageUrl, itemName, itemPrice, itemDescription, itemId, itemDetailsAnnotation, itemDetailsAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        itemDetailsImage = findViewById(R.id.itemImageDetails);
        itemDetailsName = findViewById(R.id.itemNameDetails);
        itemDetailsDescription = findViewById(R.id.itemDescriptionDetails);
        itemDetailsPrice = findViewById(R.id.itemPriceDetails);
        etItemDetailsAnnotation = findViewById(R.id.editTextAnnotationItemDetails);
        amountButton = findViewById(R.id.countItemsButton);
        addToCartButton = findViewById(R.id.buttonAddToCart);

        openCartButton = findViewById(R.id.buttonOpenCart);

        itemImageUrl = getIntent().getStringExtra("itemURL");
        itemName = getIntent().getStringExtra("itemName");
        itemPrice = getIntent().getStringExtra("itemPrice");
        itemDescription = getIntent().getStringExtra("itemDescription");
        itemId = getIntent().getStringExtra("itemId");

        initViews();
    }

    private void initViews() {

        itemDetailsName.setText(itemName);
        itemDetailsPrice.setText("$" + itemPrice);
        itemDetailsDescription.setText(itemDescription);

        Glide.with(this)
                .load(itemImageUrl)
                .into(itemDetailsImage);

        addToCartButton.setOnClickListener(v -> addItemToCart());
    }

    private void addItemToCart() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Cart List");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SimpleDateFormat simpleDateFormat;
        String itemTotalAmountPrice = "";
        int itemTotalPriceInt, itemPriceInt, itemAmountAddedInt;

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String timestamp = simpleDateFormat.format(new Date());

        itemDetailsAmount = amountButton.getNumber();
        //Sumar todos los precios del mismo producto
        try {
            itemPriceInt = Integer.parseInt(itemPrice);
            itemAmountAddedInt = Integer.parseInt(itemDetailsAmount);
            itemTotalPriceInt = itemPriceInt * itemAmountAddedInt;
            itemTotalAmountPrice = String.valueOf(itemTotalPriceInt);
        } catch(NumberFormatException nfe){
            System.out.println("Could not parse string " + nfe);
        }

        itemDetailsAnnotation = etItemDetailsAnnotation.getText().toString().trim();

        HashMap<String, String> map = new HashMap<>();
        map.put("itemId", itemId);
        map.put("annotation", itemDetailsAnnotation);
        map.put("price", itemTotalAmountPrice);
        map.put("name", itemName);
        map.put("amount", itemDetailsAmount);
        map.put("description", itemDescription);
        map.put("timestamp", timestamp);
        ref.child(userId).child(timestamp).setValue(map);

        Intent intent = new Intent(ItemDetailsActivity.this, MenuPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}