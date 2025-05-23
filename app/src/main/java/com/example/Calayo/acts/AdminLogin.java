package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Calayo.R;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;

/**
 * This activity handles user login using Firebase Authentication.
 * It checks user input, verifies credentials, and starts the splash screen on success.
 */
public class AdminLogin extends AppCompatActivity {

    private FirebaseAuth myAuth = FirebaseAuth.getInstance(); // Firebase authentication instance
    private ProgressBar progressBar; // Spinner for loading effect
    private Handler handler = new Handler(); // Handler to run tasks after a delay
    private Runnable networkTimeoutRunnable; // A timeout task if login takes too long
    tempStorage temp; // (Not used in this code, maybe used elsewhere)

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminlogin); // Load the login screen layout

        // Connect UI elements to variables
        progressBar = findViewById(R.id.progressBar);
        Button btn = findViewById(R.id.btnLogin);
        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);

        // Handle login button click
        btn.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String pass = passwordEditText.getText().toString().trim();

            // Reset error messages
            emailEditText.setError(null);
            passwordEditText.setError(null);

            // Validation: check for empty fields
            if (email.isEmpty() || pass.isEmpty()) {
                if (email.isEmpty()) emailEditText.setError("Required");
                if (pass.isEmpty()) passwordEditText.setError("Required");
                return;
            }

            // Validation: password length
            if (pass.length() < 6 || pass.length() > 16) {
                if (pass.length() < 6)
                    passwordEditText.setError("Password must be at least 6 characters");
                if (pass.length() > 16)
                    passwordEditText.setError("Password is ambiguous");
                return;
            }

            // Validation: email format
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Invalid email format");
                return;
            }

            // Show progress spinner and set a timeout
            progressBar.setVisibility(View.VISIBLE);
            networkTimeoutRunnable = () -> {
                if (progressBar.getVisibility() == View.VISIBLE) {
                    Toast.makeText(AdminLogin.this,
                            "Network seems slow or weak. Please check your connection.",
                            Toast.LENGTH_LONG).show();
                }
            };
            handler.postDelayed(networkTimeoutRunnable, 30000); // 30-second timeout

            // Call login function
            loginUser(emailEditText, passwordEditText);
        });

        // Toggle password visibility when checkbox is clicked
        CheckBox showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox);
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            // Move cursor to the end after toggling visibility
            passwordEditText.post(() -> passwordEditText.setSelection(passwordEditText.getText().length()));
        });
    }

    /**
     * Logs the user in using Firebase Authentication.
     * If successful, saves user info and moves to the splash screen.
     */
    protected void loginUser(EditText email, EditText pass) {
        String username = email.getText().toString().trim();
        String password = pass.getText().toString().trim();

        myAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, task -> {
            handler.removeCallbacks(networkTimeoutRunnable); // Cancel timeout if login finished
            progressBar.setVisibility(View.GONE); // Hide loading spinner

            if (task.isSuccessful()) {
                // Save user login state and info in local storage
                SharedPreferences preferences = getSharedPreferences("admin", MODE_PRIVATE);
                preferences.edit().putBoolean("isLoggedIn", true).apply();
                preferences.edit().putString("userName", myAuth.getCurrentUser().getUid()).apply();
                preferences.edit().putString("email", myAuth.getCurrentUser().getEmail()).apply();

                // Go to splash screen
                Intent intent = new Intent(this, admin_splash.class);
                startActivity(intent);
                finish(); // Close the login screen
            } else {
                // Retry login if failed
                this.recreate();
                if (task.getException() instanceof FirebaseNetworkException) {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Error: unknown", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Called when "Register" button is clicked
     */
    public void register(View view) {
        Intent log = new Intent(this, AdminRegister.class);
        startActivity(log);
    }

    /**
     * Called when "Back" button is clicked
     */
    public void onBackClick(View view) {
        Intent intent = new Intent(view.getContext(), admin_splash.class);
        view.getContext().startActivity(intent);
    }
}
