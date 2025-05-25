package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.Calayo.R;
import com.example.Calayo.entities.user;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A dialog that lets the admin log in using email and password.
 */
public class AdminLogin extends DialogFragment {

    // Firebase authentication
    private FirebaseAuth myAuth = FirebaseAuth.getInstance();

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStat) {
        // Load the layout for the login screen
        View view = inflater.inflate(R.layout.adminlogin, container, false);

        // Get UI elements
        Button btn = view.findViewById(R.id.btnLogin);
        EditText passwordEditText = view.findViewById(R.id.pass);
        CheckBox showPasswordCheckBox = view.findViewById(R.id.showPasswordCheckBox);

        // When login button is clicked, call loginUser()
        btn.setOnClickListener(v -> {
            loginUser(view);
        });

        // Show or hide password when checkbox is clicked
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show password as plain text
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // Hide password (show dots)
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            // Keep cursor at the end after toggling
            passwordEditText.post(() -> passwordEditText.setSelection(passwordEditText.getText().length()));
        });

        return view;
    }

    /**
     * Logs in the admin using Firebase Authentication.
     * Shows message if login fails or if there's no internet.
     */
    protected void loginUser(View btn) {
        // Get the input from username and password fields
        EditText name = btn.findViewById(R.id.username);
        String username = name.getText().toString().trim();

        EditText pass = btn.findViewById(R.id.pass);
        String password = pass.getText().toString().trim();

        // Create a user object (not used here but maybe needed later)
        user newUser = new user(username, password);

        // Try to sign in using Firebase
        myAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                // Login success
                Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_SHORT).show();

                // Go to admin dashboard
                Intent intent = new Intent(getContext(), AdminDashB.class); // Change to your actual dashboard class
                startActivity(intent);

                // Close this login dialog
                dismiss();
            } else {
                // Login failed
                Exception e = task.getException();
                if (e instanceof FirebaseNetworkException) {
                    // No internet
                    Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_LONG).show();
                } else {
                    // Other error
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Placeholder function to get the user's role (e.g., owner, admin).
     * Right now, it just returns "owner" by default.
     */
    private String getUserRole(String username) {
        // Replace this with real logic later if needed
        return "owner";
    }
}
