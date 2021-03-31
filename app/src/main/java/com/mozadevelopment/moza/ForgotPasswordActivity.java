package com.mozadevelopment.moza;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText passwordEmail;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        passwordEmail = findViewById(R.id.edit_text_password_email);
        Button resetPassword = findViewById(R.id.button_password_reset);
        firebaseAuth = FirebaseAuth.getInstance();
        String emailNotRegisteredToast = getString(R.string.emailNotRegisteredToast);
        String sentEmailToast = getString(R.string.sentEmailToast);
        String errorEmailToast = getString(R.string.errorEmailToast);

        resetPassword.setOnClickListener(v -> {
            String userEmail = passwordEmail.getText().toString().trim();

            if (userEmail.equals("")){
                Toast.makeText(ForgotPasswordActivity.this, emailNotRegisteredToast,Toast.LENGTH_SHORT).show();
            } else {
                firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(ForgotPasswordActivity.this,sentEmailToast,Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this,errorEmailToast, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}