package com.example.Calayo.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.Calayo.R;
import com.example.Calayo.acts.setUpPassword;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class userRegisterAct extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth myAuth= FirebaseAuth.getInstance();;
    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_register_act);
        EditText Email = findViewById(R.id.editTextEmail);
        EditText Name = findViewById(R.id.editTextName);
        Button btnGetStarted = findViewById(R.id.buttonSignUp);
        btnGetStarted.setOnClickListener(v -> {
            String email = Email.getText().toString().trim();
            String name = Name.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                myAuth.createUserWithEmailAndPassword(email,name).addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = myAuth.getCurrentUser();
                                Map<String,Object> data = new HashMap<>();
                                data.put("uid",user.getUid());
                                data.put("username",user.getEmail());
                                data.put("role","user");
                                db.collection("users").document(user.getUid()).set(data);
                                Toast.makeText(this, "Successfully Registered.", Toast.LENGTH_SHORT).show();
                                Intent loginAct = new Intent(this , setUpPassword.class);
                                startActivity(loginAct);
                            } else {
                                Exception e = task.getException();
                                if (e instanceof FirebaseNetworkException) {
                                    Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(this, "Error: Unknown", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        // Toggle password visibility for both fields simultaneously
//        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                // Show both passwords
//                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                conpasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//            } else {
//                // Hide both passwords
//                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                conpasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
//            }
//
//            // Set the cursor at the end of the text after toggle
//            passwordEditText.post(() -> passwordEditText.setSelection(passwordEditText.getText().length()));
//            conpasswordEditText.post(() -> conpasswordEditText.setSelection(conpasswordEditText.getText().length()));
//        });
    }
    public void login(View view){
        Intent log = new Intent(this, userLoginAct.class);
        startActivity(log);
    }
}
