package com.mozadevelopment.moza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CheckAccessLevelActivity extends AppCompatActivity {

    private ProgressBar spinner;
    private DatabaseReference UsersRef;
    String currentUserID, currentUserRole;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_access_level);

        spinner = findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            currentUserID = mFirebaseUser.getUid();
        }

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        if (mFirebaseUser != null) {

            UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String logString = "SNAPSHOT EXISTS";
                        Log.i("HELLO", logString);
                        if (dataSnapshot.hasChild("role")) {
                            currentUserRole = dataSnapshot.child("role").getValue().toString();

                            if (currentUserRole.equals("Admin")) {
                                String logString1 = "ADMIN ROLE";
                                Log.i("HELLO", logString1);
                                Intent adminIntent = new Intent (CheckAccessLevelActivity.this, AdminMainActivity.class);
                                adminIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(adminIntent);
                            } else {
                                String logString2 = "USER ROLE";
                                Log.i("HELLO", logString2);
                                Intent userIntent = new Intent(CheckAccessLevelActivity.this, MainActivity.class);
                                userIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(userIntent);
                            }
                        }
                    } else {
                        Toast.makeText(CheckAccessLevelActivity.this, "Profile name do not exists...", Toast.LENGTH_SHORT).show();
                        sendUserToLoginActivity();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) { }
            });

        } else {
            sendUserToLoginActivity();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendUserToLoginActivity();
        } else {
            CheckUserExistence();
        }
    }

    private void CheckUserExistence() {
        final String current_user_id = mAuth.getCurrentUser().getUid();
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(current_user_id)){
                    sendUserToLoginActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(CheckAccessLevelActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

}