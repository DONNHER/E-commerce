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

import androidx.appcompat.app.AppCompatActivity;

import com.example.Calayo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

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
        TextView error = findViewById(R.id.error);
        ImageView btn = findViewById(R.id.back);
        btn.setOnClickListener(view -> finish());
        btnGetStarted.setOnClickListener(v -> {
            String email = Email.getText().toString().trim();
            String input = Name.getText().toString().trim();

            // Reset previous errors
            Name.setError(null);
            Email.setError(null);

            if (input.isEmpty() || email.isEmpty()) {
                if (input.isEmpty()) Name.setError("Required");
                if (email.isEmpty()) Email.setError("Required");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Email.setError("Invalid email format");
                return;
            }

            db.collection("users").whereEqualTo("email", email).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                Email.setError("This email is already registered.");

                            } else {
                                String name = input.replaceAll("\\W","");
                                Toast.makeText(userRegisterAct.this, name, Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(userRegisterAct.this, setUpPassword.class);
                                intent.putExtra("email", email);
                                intent.putExtra("name", name);
                                startActivity(intent);
                            }
                        } else {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFirestoreException) {
                                FirebaseFirestoreException firestoreException = (FirebaseFirestoreException) e;
                                if (firestoreException.getCode() == FirebaseFirestoreException.Code.UNAVAILABLE) {
                                    Toast.makeText(userRegisterAct.this, "No internet connection", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                            Log.e("Firestore", "Error getting document", e);
                            Toast.makeText(userRegisterAct.this, "Error checking email. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }



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

    public void login(View view){
        Intent log = new Intent(this, userLoginAct.class);
        startActivity(log);
    }

}
