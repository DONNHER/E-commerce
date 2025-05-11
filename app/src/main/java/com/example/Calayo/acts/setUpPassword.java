package com.example.Calayo.acts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Calayo.R;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class setUpPassword extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth myAuth = FirebaseAuth.getInstance();
    private String email, name, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_set);

        EditText pass = findViewById(R.id.pass);
        EditText conPass = findViewById(R.id.conPass);
        Button submitBtn = findViewById(R.id.buttonSignUp); // Add a button to XML and handle click
        Button back = findViewById(R.id.back2);
        back.setOnClickListener(view -> finish());
        // Retrieve email and name from intent
        String email = getIntent().getStringExtra("email");
        String name = getIntent().getStringExtra("name");

        submitBtn.setOnClickListener(v -> {
            String password = pass.getText().toString().trim();
            String confirmPassword = conPass.getText().toString().trim();

            pass.setError(null);
            conPass.setError(null);

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                if (password.isEmpty()) pass.setError("Required");
                if (confirmPassword.isEmpty()) conPass.setError("Required");
                return;
            }

            if (password.length() < 6 ||password.length() > 16) {
                if (password.length() < 6) pass.setError("Password must be at least 6 characters");
                if (password.length() >16) pass.setError("Password is ambiguous");
                return;
            }

            if (!password.equals(confirmPassword)) {
                conPass.setError("Passwords do not match");
                return;
            }

            // Register the user
            myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = myAuth.getCurrentUser();
                    Map<String, Object> data = new HashMap<>();
                    data.put("name", name);
                    data.put("email", email);
                    data.put("role", "user");

                    db.collection("users").document(user.getUid()).set(data);

                    Toast.makeText(this, "Successfully Registered.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, userLoginAct.class));
                    finish();
                } else {
                    Exception e = task.getException();
                    if (e instanceof FirebaseNetworkException) {
                        Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        });
    }
}

