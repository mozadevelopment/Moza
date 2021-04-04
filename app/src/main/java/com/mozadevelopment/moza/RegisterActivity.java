package com.mozadevelopment.moza;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.hbb20.CountryCodePicker;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editTextEmail, editTextPassword, editTextName,editTextPhone;
    CountryCodePicker ccp;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextName = findViewById(R.id.edit_text_name);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        ccp = findViewById(R.id.ccp);
        editTextPhone = findViewById(R.id.edit_text_phone);

        ccp.registerCarrierNumberEditText(editTextPhone); //Unir codigo de pais con numero de telefono

        findViewById(R.id.button_register).setOnClickListener(this);
        findViewById(R.id.text_view_login).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    private void registerUser(){
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String phoneNeededToast = getString(R.string.phoneNeededToast);
        String emailValidToast = getString(R.string.emailValidToast);
        String passwordValidToast = getString(R.string.passwordValidToast);
        String passwordNeededToast = getString(R.string.passwordNeededToast);
        String nameNeededToast = getString(R.string.nameNeededToast);
        String alreadyRegisteredErrorToast = getString(R.string.alreadyRegisteredErrorToast);
        String emailNeededToast = getString(R.string.emailNeededToast);
        String phoneInvalidToast = getString(R.string.phoneInvalidToast);

        if (name.isEmpty()){
            editTextName.setError(nameNeededToast);
            editTextName.requestFocus();
            return;
        }

        if (phone.isEmpty()){
            editTextPhone.setError(phoneNeededToast);
            editTextPhone.requestFocus();
            return;
        }

        if (!ccp.isValidFullNumber()){
            editTextPhone.setError(phoneInvalidToast);
            editTextPhone.requestFocus();
            return;
        }

        if (email.isEmpty()){
            editTextEmail.setError(emailNeededToast);
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()){
            editTextPassword.setError(passwordNeededToast);
            editTextPassword.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError(emailValidToast);
            editTextEmail.requestFocus();
            return;
        }

        if (password.length()<6) {
            editTextPassword.setError(passwordValidToast);
            editTextPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                finish();
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            } else {
                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    Toast.makeText(getApplicationContext(), alreadyRegisteredErrorToast, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_register:
                registerUser();
                break;

            case R.id.text_view_login:
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }
}

