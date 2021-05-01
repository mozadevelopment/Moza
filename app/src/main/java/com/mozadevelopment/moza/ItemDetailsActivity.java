package com.mozadevelopment.moza;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import java.io.StringBufferInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ItemDetailsActivity extends AppCompatActivity {

    ImageView itemDetailsImage;
    TextView itemDetailsName, itemDetailsDescription, itemDetailsPrice;
    Button addToCartButton, openCartButton;
    ElegantNumberButton amountButton;
    EditText etItemDetailsAnnotation;
    int amountAddedInt, amountInCart;
    String itemsInCart, itemImageUrl, itemName, itemPrice, itemDescription, itemId, itemDetailsAnnotation, itemDetailsAmount;

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

        openCartButton = findViewById(R.id.buttonOpenCart); //Menu activity button

        itemImageUrl = getIntent().getStringExtra("itemURL");
        itemName = getIntent().getStringExtra("itemName");
        itemPrice = getIntent().getStringExtra("itemPrice");
        itemDescription = getIntent().getStringExtra("itemDescription");
        itemId = getIntent().getStringExtra("itemId");
        itemsInCart = getIntent().getStringExtra("itemsInCart");

        try {
            amountInCart = Integer.parseInt(itemsInCart);
        } catch(NumberFormatException nfe){
            System.out.println("Could not parse string " + nfe);
        }

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
        String amountAdded, itemTotalAmountPrice = "";
        int totalAmount, itemTotalPriceInt, itemPriceInt, itemAmountAddedInt;

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String timestamp = simpleDateFormat.format(new Date());

        itemDetailsAmount = amountButton.getNumber();
        //Get total price for same item.
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
        ref.child(userId).child(timestamp).setValue(map);


        //Send amount of items in cart to main menu activity
        amountAdded = itemDetailsAmount;
        try {
            amountAddedInt = Integer.parseInt(amountAdded);
            totalAmount = amountAddedInt + amountInCart;
            amountAdded = String.valueOf(totalAmount);
        } catch(NumberFormatException nfe){
            System.out.println("Could not parse string " + nfe);
        }

        Intent intent = new Intent(ItemDetailsActivity.this, MenuPageActivity.class);
        intent.putExtra("amountItemsAdded", amountAdded);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}