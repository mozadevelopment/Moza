package com.mozadevelopment.moza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

public class ItemDetailsActivity extends AppCompatActivity {

    private ImageView itemDetailsImage;
    private TextView itemDetailsName, itemDetailsDescription, itemDetailsPrice;
    private Button addToCartButton;
    private ElegantNumberButton amountButton;
    private EditText etItemDetailsAnnotation;
    private String itemDetailsAnnotation, itemDetailsAmount, itemDescriptionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        initViews();
    }

    private void initViews() {

        itemDetailsImage = findViewById(R.id.itemImageDetails);
        itemDetailsName = findViewById(R.id.itemNameDetails);
        itemDetailsDescription = findViewById(R.id.itemDescriptionDetails);
        itemDetailsPrice = findViewById(R.id.itemPriceDetails);
        etItemDetailsAnnotation = findViewById(R.id.editTextAnnotationItemDetails);
        amountButton = findViewById(R.id.countItemsButton);
        addToCartButton = findViewById(R.id.buttonAddToCart);

        final String itemImageUrl = getIntent().getStringExtra("itemURL");
        final String itemName = getIntent().getStringExtra("itemName");
        final String itemPrice = getIntent().getStringExtra("itemPrice");
        final String itemDescription = getIntent().getStringExtra("itemDescription");
        final String itemId = getIntent().getStringExtra("itemId");

        itemDetailsName.setText(itemName);
        itemDetailsPrice.setText("$"+itemPrice);
        itemDetailsDescription.setText(itemDescription);
        itemDescriptionId = itemId;

        Glide.with(this)
                .load(itemImageUrl)
                .into(itemDetailsImage);

        addToCartButton.setOnClickListener(v -> {

            itemDetailsAmount = amountButton.getNumber();
            itemDetailsAnnotation = etItemDetailsAnnotation.getText().toString().trim();

            Intent intent = new Intent(ItemDetailsActivity.this, MenuPageActivity.class);
            startActivity(intent);

        });
    }
}