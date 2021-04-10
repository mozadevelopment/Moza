package com.mozadevelopment.moza;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;


public class MainActivity extends AppCompatActivity {

    Button qr, makeAnOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qr = (Button)findViewById(R.id.button_qr);
        makeAnOrder = (Button)findViewById(R.id.button_make_an_order);

        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new IntentIntegrator(MainActivity.this).initiateScan();

            }


        });
    }
}