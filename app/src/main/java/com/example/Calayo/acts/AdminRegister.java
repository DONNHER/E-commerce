package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.method.*;
import android.util.Patterns;
import android.view.*;
import android.widget.*;

import androidx.fragment.app.DialogFragment;

import com.example.Calayo.R;
import com.example.Calayo.entities.user;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * A dialog for admin registration using email and password.
 */
public class AdminRegister extends DialogFragment {

    // Firebase Firestore for saving user data
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Firebase Authentication
    private final FirebaseAuth myAuth = FirebaseAuth.getInstance();

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStat) {
        // Load the layout for admin registration
        View view = inflater.inflate(R.layout.admin_register, container, false);

        // Get input fields
        EditText name = view.findViewById(R.id.username);
        EditText passwordEditText = view.findViewById(R.id.pass);
        EditText conpasswordEditText = view.findViewById(R.id.conPass);
        CheckBox showPasswordCheckBox = view.findViewById(R.id.showPasswordCheckBox);
        Button btnGetStarted = view.findViewById(R.id.btnSignUp);

        // When sign up button is clicked
        btnGetStarted.setOnClickListener(v -> {
            String username = name.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmation = conpasswordEditText.getText().toString().trim();

            // Check if fields are filled
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            }
            // Check password length
            else if (password.length() < 6) {
                Toast.makeText(getContext(), "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
            }
            // Check if passwords match
            else if (!password.equals(confirmation)) {
                Toast.makeText(getContext(), "Passwords do not match.", Toast.LENGTH_SHORT).show();
            }
            // Check if email format is valid
            else if (Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                // Register user using Firebase
                myAuth.createUserWithEmailAndPassword(username, password)
                        .addOnCompleteListener(requireActivity(), task -> {
                            if (task.isSuccessful()) {
                                // Save user data to Firestore
                                FirebaseUser user = myAuth.getCurrentUser();
                                Map<String, Object> data = new HashMap<>();
                                data.put("uid", user.getUid());
                                data.put("username", user.getEmail());
                                data.put("role", "admin");

                                db.collection("users").document(user.getUid()).set(data);

                                Toast.makeText(getContext(), "Successfully Registered.", Toast.LENGTH_SHORT).show();
                                dismiss();

                                // Optional: switch to login screen after registration
                                // new userLoginAct().show(getParentFragmentManager(), "LogInDialog");
                            } else {
                                // Show error message
                                Exception e = task.getException();
                                if (e instanceof FirebaseNetworkException) {
                                    Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            } else {
                // If email is invalid, still create a user object (not saved in Firebase)
                user newUser = new user(username, password);
                Toast.makeText(getContext(), "Successfully Registered.", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        // Show or hide passwords when checkbox is checked
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show password text
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                conpasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // Hide password (show dots)
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                conpasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }

            // Keep cursor at the end after toggling visibility
            passwordEditText.post(() -> passwordEditText.setSelection(passwordEditText.getText().length()));
            conpasswordEditText.post(() -> conpasswordEditText.setSelection(conpasswordEditText.getText().length()));
        });

        return view;
    }
}
