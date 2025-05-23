package com.example.Calayo.acts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Calayo.R;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class change_pass extends AppCompatActivity {

    private static final String TAG = "SetUpPasswordActivity";

    private FirebaseFirestore db;
    private FirebaseAuth myAuth;

    private EditText passField, conPassField;
    private Button submitBtn, backBtn;

    private String email, name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepass);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        myAuth = FirebaseAuth.getInstance();

        // Retrieve extras from intent
        email = getIntent().getStringExtra("email");
        name = getIntent().getStringExtra("name");

        if (email == null || name == null) {
            Log.e(TAG, "Missing email or name in intent extras");
            Toast.makeText(this, "Missing account details. Please try again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize UI components
        passField = findViewById(R.id.pass);
        conPassField = findViewById(R.id.conPass);
        conPassField = findViewById(R.id.conPass);
        submitBtn = findViewById(R.id.buttonSignUp);
        backBtn = findViewById(R.id.back2);

        // Handle back button click
        backBtn.setOnClickListener(view -> finish());

        // Handle submit click
        submitBtn.setOnClickListener(view -> handlePasswordSetup());
    }

    /**
     * Handles password validation and user registration.
     */
    private void handlePasswordSetup() {
        String password = passField.getText().toString().trim();
        String confirmPassword = conPassField.getText().toString().trim();

        passField.setError(null);
        conPassField.setError(null);

        // Validate input
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            if (password.isEmpty()) passField.setError("Required");
            if (confirmPassword.isEmpty()) conPassField.setError("Required");
            return;
        }

        if (password.length() < 6 || password.length() > 16) {
            if (password.length() < 6) passField.setError("Password must be at least 6 characters");
            else passField.setError("Password must not exceed 16 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            conPassField.setError("Passwords do not match");
            return;
        }

        // Create user with Firebase
        myAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = myAuth.getCurrentUser();
                        if (user == null) {
                            Log.e(TAG, "Firebase user is null after registration");
                            Toast.makeText(this, "Registration failed. Try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        Map<String, Object> data = new HashMap<>();
                        data.put("name", name);
                        data.put("email", email);
                        data.put("role", "user");

                        db.collection("users").document(user.getUid()).set(data)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Successfully Registered.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, userLoginAct.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Firestore write failed: " + e.getMessage(), e);
                                    Toast.makeText(this, "Registration failed: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                });
                    } else {
                        Exception e = task.getException();
                        if (e instanceof FirebaseNetworkException) {
                            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
                        } else {
                            Log.e(TAG, "FirebaseAuth error: " + e.getMessage(), e);
                            Toast.makeText(this, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
