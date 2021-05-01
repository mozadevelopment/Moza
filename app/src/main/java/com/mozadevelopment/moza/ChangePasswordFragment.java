package com.mozadevelopment.moza;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ChangePasswordFragment extends Fragment {

    Button confirm;
    TextInputEditText currentPassword, newPassword, confirmPassword;
    String uid;
    DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_change_password, container, false);
        currentPassword = rootView.findViewById(R.id.text_input_current_password);
        newPassword = rootView.findViewById(R.id.text_input_new_password);
        confirmPassword = rootView.findViewById(R.id.text_input_confirm_password);
        confirm = rootView.findViewById(R.id.button_confirm);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        confirm.setOnClickListener(v -> {
            if (!validatePassword()) {
                return;
            } else {
                String new_password = newPassword.getText().toString();
                HashMap<String, Object> userMap = new HashMap<>();
                userMap.put("password", new_password);
                mDatabase.updateChildren(userMap);
            }
        });

        return rootView;
    }

    private boolean validatePassword() {
        String val3 = currentPassword.getText().toString().trim();
        String val2 = confirmPassword.getText().toString().trim();
        String val = newPassword.getText().toString().trim();
        String checkValidPassword = "^" +
                "(?=.*[0-9])" + //por lo menos un caracter
                "(?=\\S+$)" + // sin espacios en blanco
                ".{6,}" + //por lo menos 6 caracteres
                "$";
        String passwordValidToast = getString(R.string.passwordValidToast);
        String passwordNeededToast = getString(R.string.passwordNeededToast);
        String passwordMatchToast = getString(R.string.passwordMatchToast);

        if (val.isEmpty()) {
            newPassword.setError(passwordNeededToast);
            return false;
        } else if (val2.isEmpty()) {
            confirmPassword.setError(passwordNeededToast);
            return false;
        } else if (val3.isEmpty()) {
            currentPassword.setError(passwordNeededToast);
            return false;
        } else if (!val.matches(checkValidPassword)) {
            newPassword.setError(passwordValidToast);
            return false;
        } else if (!val.equals(val2)) {
            newPassword.setError(passwordMatchToast);
            confirmPassword.setError(passwordMatchToast);
            return false;
        } else {
            newPassword.setError(null);
            return true;
        }
    }
}
