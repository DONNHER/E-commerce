package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Calayo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class AdminRegister extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth myAuth = FirebaseAuth.getInstance();

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_register_act);

        EditText Email = findViewById(R.id.editTextEmail);
        EditText Name = findViewById(R.id.editTextName);
        Button btnGetStarted = findViewById(R.id.buttonSignUp);
        ImageView btn = findViewById(R.id.back);

        // Go back to the previous screen
        btn.setOnClickListener(view -> finish());

        // Sign-up button handler
        btnGetStarted.setOnClickListener(v -> {
            String email = Email.getText().toString().trim();
            String input = Name.getText().toString().trim();

            // Reset previous input errors
            Name.setError(null);
            Email.setError(null);

            // Basic input validation
            if (input.isEmpty() || email.isEmpty()) {
                if (input.isEmpty()) Name.setError("Required");
                if (email.isEmpty()) Email.setError("Required");

                logEvent("VALIDATION_FAIL", "Missing name or email", null, "userRegisterAct");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Email.setError("Invalid email format");
                logEvent("VALIDATION_FAIL", "Invalid email format: " + email, null, "userRegisterAct");
                return;
            }

            // Check if email is already registered in Firestore
            db.collection("users").document("admin").collection("account").whereEqualTo("email", email).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                // Duplicate email
                                Email.setError("This email is already registered.");
                                logEvent("DUPLICATE_EMAIL", "Attempted to register with already-used email: " + email, null, "userRegisterAct");

                            } else {
                                // Clean name for next step
                                String name = input.replaceAll("\\W", "");
                                Toast.makeText(AdminRegister.this, name, Toast.LENGTH_LONG).show();

                                logEvent("REGISTER_STEP_SUCCESS", "Proceeding to password setup for email: " + email, null, "userRegisterAct");

                                // Go to password setup activity
                                Intent intent = new Intent(AdminRegister.this, setUpPassword_Admin.class);
                                intent.putExtra("email", email);
                                intent.putExtra("name", name);
                                startActivity(intent);
                            }
                        } else {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFirestoreException) {
                                FirebaseFirestoreException firestoreException = (FirebaseFirestoreException) e;
                                if (firestoreException.getCode() == FirebaseFirestoreException.Code.UNAVAILABLE) {
                                    Toast.makeText(AdminRegister.this, "No internet connection", Toast.LENGTH_LONG).show();
                                    logEvent("FIRESTORE_UNAVAILABLE", "No internet connection", null, "userRegisterAct");
                                    return;
                                }
                            }
                            Log.e("Firestore", "Error getting document", e);
                            Toast.makeText(AdminRegister.this, "Error checking email. Please try again.", Toast.LENGTH_SHORT).show();
                            logEvent("FIRESTORE_ERROR", "Error checking email: " + e.getMessage(), null, "userRegisterAct");
                        }
                    });
        });
    }

    /**
     * Logs the event to Firestore for backend diagnostics and auditing.
     *
     * @param eventType Type of event (e.g., VALIDATION_FAIL, REGISTER_STEP_SUCCESS, FIRESTORE_ERROR)
     * @param message Detailed message about the event
     * @param userId Firebase UID or null
     * @param context Class or activity name (optional)
     */
    private void logEvent(String eventType, String message, @Nullable String userId, @Nullable String context) {
        Map<String, Object> log = new HashMap<>();
        log.put("timestamp", FieldValue.serverTimestamp());
        log.put("event_type", eventType);
        log.put("message", message);
        if (userId != null) log.put("user_id", userId);
        if (context != null) log.put("context", context);

        db.collection("logs").add(log)
                .addOnSuccessListener(documentReference -> Log.d("LogEvent", "Log saved: " + eventType))
                .addOnFailureListener(e -> Log.e("LogEvent", "Failed to save log: " + e.getMessage()));
    }

    /**
     * Redirects to the login activity.
     */
    public void login(View view) {
        Intent log = new Intent(this, userLoginAct.class);
        startActivity(log);
        logEvent("REDIRECT", "Redirected to login screen", null, "userRegisterAct");
    }
}
