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

public class userLoginAct extends AppCompatActivity {

    private FirebaseAuth myAuth = FirebaseAuth.getInstance();
    tempStorage temp;

    private ProgressBar progressBar;
    private Handler handler = new Handler();
    private Runnable networkTimeoutRunnable;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login_act);

        progressBar = findViewById(R.id.progressBar);
        Button btn = findViewById(R.id.btnLogin);
        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);

        btn.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String pass = passwordEditText.getText().toString().trim();

            emailEditText.setError(null);
            passwordEditText.setError(null);

            if (email.isEmpty() || pass.isEmpty()) {
                if (email.isEmpty()) emailEditText.setError("Required");
                if (pass.isEmpty()) passwordEditText.setError("Required");
                return;
            }
            if (pass.length() < 6 || pass.length() > 16) {
                if (pass.length() < 6)
                    passwordEditText.setError("Password must be at least 6 characters");
                if (pass.length() > 16)
                    passwordEditText.setError("Password is ambiguous");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Invalid email format");
                return;
            }
            // Show spinner and start login
            progressBar.setVisibility(View.VISIBLE);

            // Start 30 seconds timeout to check for weak network
            networkTimeoutRunnable = () -> {
                if (progressBar.getVisibility() == View.VISIBLE) {
                    Toast.makeText(userLoginAct.this,
                            "Network seems slow or weak. Please check your connection.",
                            Toast.LENGTH_LONG).show();
                }
            };
            handler.postDelayed(networkTimeoutRunnable, 30000); // 30,000 ms = 30 seconds

            loginUser(emailEditText, passwordEditText);
        });

        CheckBox showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox);
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            passwordEditText.post(() -> passwordEditText.setSelection(passwordEditText.getText().length()));
        });
    }

    protected void loginUser(EditText email, EditText pass) {
        String username = email.getText().toString().trim();
        String password = pass.getText().toString().trim();
        myAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, task -> {
            // Cancel the network timeout callback when login completes
            handler.removeCallbacks(networkTimeoutRunnable);
            // Hide spinner when login completes
            progressBar.setVisibility(View.GONE);

            if (task.isSuccessful()) {
                SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
                preferences.edit().putBoolean("isLoggedIn", true).apply();
                preferences.edit().putString("userName", myAuth.getCurrentUser().getUid()).apply();
                Toast.makeText(this, preferences.getString("userName", ""), Toast.LENGTH_SHORT).show();
                preferences.edit().putString("email", myAuth.getCurrentUser().getEmail()).apply();
                Intent intent = new Intent(this, splash.class);
                startActivity(intent);
                finish();
            } else {
                Exception e = task.getException();
                this.recreate();
                if (e instanceof FirebaseNetworkException) {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Error: unknown", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void register(View view) {
        Intent log = new Intent(this, userRegisterAct.class);
        startActivity(log);
    }

    public void onBackClick(View view) {
        Intent intent = new Intent(view.getContext(), main_act.class);
        view.getContext().startActivity(intent);
    }
}
