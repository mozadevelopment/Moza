package com.mozadevelopment.moza;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {
    
    private FirebaseAuth mAuth;
    EditText editTextEmail, editTextPassword;
    TextView forgotPassword;
    String email, password;
    Button registerButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        forgotPassword = findViewById(R.id.text_view_forgot_password);
        registerButton = findViewById(R.id.button_register);
        loginButton = findViewById(R.id.button_login);

        mAuth = FirebaseAuth.getInstance();

        forgotPassword.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));

        registerButton.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        loginButton.setOnClickListener(view -> userLogin());
    }

    private void userLogin(){
        email = editTextEmail.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();
        String userNotFound = getString(R.string.userNotFoundToast);

        if (!validateEmail() | !validatePassword()){
            return;
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, CheckAccessLevelActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), userNotFound, Toast.LENGTH_SHORT).show();
                }

            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, CheckAccessLevelActivity.class));
        }
    }

    //Validation functions

    private boolean validateEmail(){
        String val = editTextEmail.getText().toString().trim();
        String checkValidEmail = "^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$";
        String emailValidToast = getString(R.string.emailValidToast);
        String emailNeededToast = getString(R.string.emailNeededToast);

        if (val.isEmpty()){
            editTextEmail.setError(emailNeededToast);
            return false;
        } else if (!val.matches(checkValidEmail)) {
            editTextEmail.setError(emailValidToast);
            return false;
        } else {
            editTextEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword(){
        String val = editTextPassword.getText().toString().trim();
        String checkValidPassword = "^" +
                "(?=.*[0-9])" + //por lo menos un caracter
                "(?=\\S+$)" + // sin espacios en blanco
                ".{6,}" + //por lo menos 6 caracteres
                "$";
        String passwordValidToast = getString(R.string.passwordValidToast);
        String passwordNeededToast = getString(R.string.passwordNeededToast);

        if (val.isEmpty()){
            editTextPassword.setError(passwordNeededToast);
            return false;
        } else if (!val.matches(checkValidPassword)) {
            editTextPassword.setError(passwordValidToast);
            return false;
        } else {
            editTextPassword.setError(null);
            return true;
        }
    }
}

