package com.example.Calayo.fragments;

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

public class AdminRegister extends DialogFragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth myAuth= FirebaseAuth.getInstance();;
    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStat) {
        View view = inflater.inflate(R.layout.admin_register, container, false);

        EditText name = view.findViewById(R.id.username);
        EditText passwordEditText = view.findViewById(R.id.pass);
        EditText conpasswordEditText = view.findViewById(R.id.conPass);
        CheckBox showPasswordCheckBox = view.findViewById(R.id.showPasswordCheckBox);
        Button btnGetStarted = view.findViewById(R.id.btnSignUp);
        btnGetStarted.setOnClickListener(v -> {
            String username = name.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmation = conpasswordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(getContext(), "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmation)) {
                Toast.makeText(getContext(), "Passwords do not match.", Toast.LENGTH_SHORT).show();
            } else if (Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                myAuth.createUserWithEmailAndPassword(username, password)
                        .addOnCompleteListener(requireActivity(), task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = myAuth.getCurrentUser();
                                Map<String,Object> data = new HashMap<>();
                                data.put("uid",user.getUid());
                                data.put("username",user.getEmail());
                                data.put("role","admin");
                                db.collection("users").document(user.getUid()).set(data);
                                Toast.makeText(getContext(), "Successfully Registered.", Toast.LENGTH_SHORT).show();
                                dismiss();
                                new userLoginAct().show(getParentFragmentManager(), "LogInDialog");
                            } else {
                                Exception e = task.getException();
                                if (e instanceof FirebaseNetworkException) {
                                    Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            } else {
                user newUser = new user(username, password);
               Toast.makeText(getContext(), "Successfully Registered.", Toast.LENGTH_SHORT).show();
                dismiss();
                new userLoginAct().show(getParentFragmentManager(), "LogInDialog");
            }
        });
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                conpasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                conpasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            passwordEditText.post(() -> passwordEditText.setSelection(passwordEditText.getText().length()));
            conpasswordEditText.post(() -> conpasswordEditText.setSelection(conpasswordEditText.getText().length()));
        });
        return view;
    }
}
