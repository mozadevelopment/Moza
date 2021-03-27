package com.mozadevelopment.moza;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener  {
    
    private FirebaseAuth mAuth;
    EditText editTextEmail, editTextPassword;
    TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        forgotPassword = findViewById(R.id.text_view_forgot_password);

        findViewById(R.id.text_view_register).setOnClickListener(this);
        findViewById(R.id.button_login).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        forgotPassword.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));

    }

    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()){
            editTextEmail.setError("Se requiere un email.");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()){
            editTextPassword.setError("Se requiere una contraseña.");
            editTextPassword.requestFocus();
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


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    finish();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Usuario no registrado o Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_view_register:
                finish();
                startActivity(new Intent(this, RegisterActivity.class));
                break;

            case R.id.button_login:
                userLogin();
                break;
        }
    }
}

