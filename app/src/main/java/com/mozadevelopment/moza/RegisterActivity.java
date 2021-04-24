package com.mozadevelopment.moza;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;
import com.mozadevelopment.moza.Database.UserHelperClass;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    EditText editTextEmail, editTextPassword, editTextName,editTextPhone;
    String name, email, phone, phoneNumber, password;
    CountryCodePicker ccp;
    FirebaseAuth mAuth;
    private Button registerButton;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private static final String USERS = "Users";
    private static final String role = "User";
    private UserHelperClass user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextName = findViewById(R.id.edit_text_name);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        ccp = findViewById(R.id.ccp);
        editTextPhone = findViewById(R.id.edit_text_phone);
        registerButton = findViewById(R.id.button_register);
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference(USERS);
        mAuth = FirebaseAuth.getInstance();

        ccp.registerCarrierNumberEditText(editTextPhone); //Unir codigo de pais con numero de telefono

        registerButton.setOnClickListener(v -> {
            if (!validateName() | !validateEmail() | !validatePassword() | !validatePhone()){
                return;
            } else {
                name = editTextName.getText().toString();
                email = editTextEmail.getText().toString();
                phone = editTextPhone.getText().toString();
                phoneNumber = ccp.getSelectedCountryCodeWithPlus() + phone; //Agregar codigo de pais al telefono
                password = editTextPassword.getText().toString();

                user = new UserHelperClass(name, email, phoneNumber, password, role); //se usa los setters y getters de la clase de ayuda, y se asigna los valores a user
                registerUser();
            }
        });

    }

   public void registerUser(){

       String alreadyRegisteredErrorToast = getString(R.string.alreadyRegisteredErrorToast);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                updateUI(user);
            } else {
                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    Toast.makeText(getApplicationContext(), alreadyRegisteredErrorToast, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void updateUI(FirebaseUser currentUser) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(USERS).child(userId);
        dbRef.setValue(user); //Se agrega data de user a la bd
        Intent loginIntent = new Intent(this, MainActivity.class);

        startActivity(loginIntent);
    }


    /* Funciones para validar datos */

    private boolean validateName() {
        String val = editTextName.getText().toString().trim();
        String nameNeededToast = getString(R.string.nameNeededToast);

        if (val.isEmpty()){
            editTextName.setError(nameNeededToast);
            return false;
        } else {
            editTextName.setError(null);
            return true;
        }
    }

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

    private boolean validatePhone(){
        String val = editTextPhone.getText().toString().trim();
        String phoneNumber = ccp.getSelectedCountryCodeWithPlus() + val;
        String phoneNeededToast = getString(R.string.phoneNeededToast);
        String phoneInvalidToast = getString(R.string.phoneInvalidToast);

        if (phoneNumber.isEmpty()){
            editTextPhone.setError(phoneNeededToast);
            return false;
        } else if (!ccp.isValidFullNumber()){
            editTextPhone.setError(phoneInvalidToast);
            return false;
        } else {
            editTextPhone.setError(null);
            return true;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_view_login:
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }
}
