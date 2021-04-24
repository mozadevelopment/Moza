package com.mozadevelopment.moza;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.function.ToDoubleBiFunction;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener  {
    
    private FirebaseAuth mAuth;
    EditText editTextEmail, editTextPassword;
    TextView forgotPassword;
    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        forgotPassword = findViewById(R.id.text_view_forgot_password);

        findViewById(R.id.button_register).setOnClickListener(this);
        findViewById(R.id.button_login).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        forgotPassword.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));

    }

    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String emailNeeded = getString(R.string.emailNeededToast);
        String emailValid = getString(R.string.emailValidToast);
        String passwordValid = getString(R.string.passwordValidToast);
        String passwordNeeded = getString(R.string.passwordNeededToast);
        String userNotFound = getString(R.string.userNotFoundToast);

        if (email.isEmpty()){
            editTextEmail.setError(emailNeeded);
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()){
            editTextPassword.setError(emailValid);
            editTextPassword.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError(passwordNeeded);
            editTextEmail.requestFocus();
            return;
        }

        if (password.length()<6) {
            editTextPassword.setError(passwordValid);
            editTextPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //checkAccessLevel();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), userNotFound, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            Log.i("UserId", userId);
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void checkAccessLevel(){

        /*//TODO - Check admin role to access different main activity.

// Get a reference to our posts
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users");
        String userId = mAuth.getCurrentUser().getUid();
        System.out.println(userId);

// Attach a listener to read the data at our posts reference

        FirebaseDatabase.getInstance().getReference(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(userId)){
                    role = snapshot.getValue("Role");
                }
                Map<String, String> map = (Map<String, String>) snapshot.getValue();
                role = map.get("Role");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });

        System.out.println("hi1");

        if (role != null) {
            if (role == "Admin") {
                System.out.println("hi");
                startActivity(new Intent(this, AdminMainActivity.class));
            } else if (role == "User"){
                startActivity(new Intent(this, MainActivity.class));
            }
        }*/
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_register:
                finish();
                startActivity(new Intent(this, RegisterActivity.class));
                break;

            case R.id.button_login:
                userLogin();
                break;
        }
    }
}

