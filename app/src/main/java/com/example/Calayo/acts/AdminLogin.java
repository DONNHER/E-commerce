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

public class AdminLogin  extends DialogFragment {
    private FirebaseAuth myAuth= FirebaseAuth.getInstance();;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStat) {
        View view = inflater.inflate(R.layout.adminlogin, container, false);

        Button btn= view.findViewById(R.id.btnLogin);
        EditText passwordEditText = view.findViewById(R.id.pass);
        CheckBox showPasswordCheckBox = view.findViewById(R.id.showPasswordCheckBox);
        btn.setOnClickListener(v -> {
            loginUser(view);
        });

        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            passwordEditText.post(() -> passwordEditText.setSelection(passwordEditText.getText().length()));
        });
        return view;
    }

    protected void loginUser(View btn) {
        EditText name =  btn.findViewById(R.id.username);
        String username = name.getText().toString().trim();
        EditText pass =  btn.findViewById(R.id.pass);
        String password = pass.getText().toString().trim();
        user newUser =   new user(username, password);
        myAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(requireActivity(), task -> {
            if(task.isSuccessful()){
                Toast.makeText(getContext(),"Login Successful",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), AdminDashB.class); // Replace with actual target
                startActivity(intent);
                dismiss();
            } else {
                Exception e = task.getException();
                if (e instanceof FirebaseNetworkException) {
                    Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private String getUserRole(String username) {
        // Replace this with real logic to fetch user role
        return "owner";
    }

}
