package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Calayo.R;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;

public class userLoginAct extends AppCompatActivity {

    private FirebaseAuth myAuth= FirebaseAuth.getInstance();
    tempStorage temp;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login_act);

        Button btn= findViewById(R.id.btnLogin);

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
            }if (pass.length() < 6 || pass.length() >16 ) {
                if (pass.length() < 6) passwordEditText.setError("Password must be at least 6 characters");
                if (pass.length() >16) passwordEditText.setError("Password is ambiguous");
                return;
            }if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Invalid email format");
                return;
            }
            loginUser(emailEditText,passwordEditText);
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
        myAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Toast.makeText(this,"Login Successful",Toast.LENGTH_SHORT).show();
                SharedPreferences preferences = getSharedPreferences("user",MODE_PRIVATE);
                preferences.edit().putBoolean("isLoggedIn",true).apply();
                preferences.edit().putString("userName",myAuth.getCurrentUser().getUid());
                preferences.edit().putString("email",myAuth.getCurrentUser().getEmail());
                Intent intent = new Intent(this, UserDashboardAct.class);
                startActivity(intent);
                finish();
            }else{
                Exception e = task.getException();
                this.recreate();
                if (e instanceof FirebaseNetworkException) {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Error: unknown" , Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void register(View view){
        Intent log = new Intent(this, userRegisterAct.class);
        startActivity(log);
    }
    public void onBackClick(View view) {
        Intent intent = new Intent(view.getContext(), main_act.class);
        view.getContext().startActivity(intent);
    }
}
