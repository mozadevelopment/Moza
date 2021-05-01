package com.mozadevelopment.moza;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class HomeFragment extends Fragment {

    private Button qr, makeAnOrder;
    private TextView textViewQr; //cuando quede la parte del scanner con el menu quitar esto

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        qr = rootView.findViewById(R.id.button_qr_scan);
        makeAnOrder = rootView.findViewById(R.id.button_make_an_order);
        textViewQr = rootView.findViewById(R.id.text_view_qr);

        qr.setOnClickListener(v -> scan());

        makeAnOrder.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MenuPageActivity.class);
            startActivity(intent);
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
