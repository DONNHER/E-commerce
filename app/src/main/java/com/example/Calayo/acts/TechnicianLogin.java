package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.*;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.DialogFragment;
import com.example.Calayo.R;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;

public class TechnicianLogin extends DialogFragment {
    private final FirebaseAuth myAuth = FirebaseAuth.getInstance();

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStat) {
        View view = inflater.inflate(R.layout.manager_login, container, false);

        TextView btm_signup = view.findViewById(R.id.bot_signUp);
        Button btn = view.findViewById(R.id.btnLogin);
        EditText passwordEditText = view.findViewById(R.id.pass);
        CheckBox showPasswordCheckBox = view.findViewById(R.id.showPasswordCheckBox);

        btn.setOnClickListener(v -> loginUser(view));

        // Toggle password visibility
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            else
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passwordEditText.post(() -> passwordEditText.setSelection(passwordEditText.getText().length()));
        });

        // Open registration dialog
        btm_signup.setOnClickListener(v -> {
            ManagerRegister dialogFragment = new ManagerRegister();
            dialogFragment.show(getParentFragmentManager(), "RegisterDialog");
            dismiss();
        });

        return view;
    }

    // Login using Firebase Authentication
    protected void loginUser(View view) {
        EditText name = view.findViewById(R.id.username);
        String username = name.getText().toString().trim();

        EditText pass = view.findViewById(R.id.pass);
        String password = pass.getText().toString().trim();

        myAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), ManagerDashB.class);
                startActivity(intent);
                dismiss();
            } else {
                Exception e = task.getException();
                if (e instanceof FirebaseNetworkException)
                    Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
