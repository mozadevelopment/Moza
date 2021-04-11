package com.mozadevelopment.moza;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private Button qr, makeAnOrder;
    private TextView textViewQr, textViewQrFail;
    String integratorPrompt = "Scan QR code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qr = (Button)findViewById(R.id.button_qr);
        makeAnOrder = (Button)findViewById(R.id.button_make_an_order);
        textViewQr = findViewById(R.id.text_view_qr);
        textViewQrFail = findViewById(R.id.text_view_qr);

        qr.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null)
            if (result.getContents() != null){
                textViewQr.setText(result.getContents());
            }else if (result.getContents() == null){
            textViewQr.setText(R.string.textViewQr);
            }else{
                textViewQrFail.setText(R.string.textViewQrFail);
            }
    }

    public void scan(){
        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.setOrientationLocked(false);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setPrompt(getString(R.string.integratorPrompt));
        integrator.initiateScan();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button_qr:
                    scan();
                    break;
            }
        }
    };

}