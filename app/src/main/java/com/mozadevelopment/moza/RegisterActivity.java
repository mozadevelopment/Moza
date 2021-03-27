package com.mozadevelopment.moza;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editTextEmail, editTextPassword, editTextName;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextName = (EditText) findViewById(R.id.edit_text_name);
        editTextEmail = (EditText) findViewById(R.id.edit_text_email);
        editTextPassword = (EditText) findViewById(R.id.edit_text_password);

        findViewById(R.id.button_register).setOnClickListener(this);
        findViewById(R.id.text_view_login).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    private void registerUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String name = editTextName.getText().toString().trim();

        if (email.isEmpty()){
            editTextEmail.setError("Se requiere un email.");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()){
            editTextEmail.setError("Se requiere una contraseña.");
            editTextEmail.requestFocus();
            return;
        }

        if (name.isEmpty()){
            editTextName.setError("Ingresa tu nombre.");
            editTextName.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Ingresa un email válido.");
            editTextEmail.requestFocus();
            return;
        }

        if (password.length()<6) {
            editTextPassword.setError("Ingrese una contraseña mayor a 6 caracteres.");
            editTextPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    finish();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                } else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "Ya estas registrado", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }

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

