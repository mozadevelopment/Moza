package com.mozadevelopment.moza;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.integration.android.IntentIntegrator;

public class HomeFragment extends Fragment {

    private Button qr, makeAnOrder;
    private TextView textViewQr; //cuando quede la parte del scanner con el menu quitar esto


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        qr = (Button) rootView.findViewById(R.id.button_qr_scan);
        makeAnOrder = (Button) rootView.findViewById(R.id.button_make_an_order);
        textViewQr = rootView.findViewById(R.id.text_view_qr);


        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });

        return rootView;
    }

    public void scan() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(HomeFragment.this);
        integrator.setOrientationLocked(false);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setPrompt(getString(R.string.integratorPrompt));
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_qr_scan:
                    scan();
                    break;
                case R.id.button_make_an_order:
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
            if (result.getContents() != null) {
                textViewQr.setText(result.getContents());
            } else if (result.getContents() == null) {
                Toast.makeText(getContext(), R.string.scanCanceledToast, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.qrScanFailedToast, Toast.LENGTH_SHORT).show();
            }
    }
}
