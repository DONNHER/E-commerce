package com.example.Calayo.acts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Calayo.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtpConfirmation extends AppCompatActivity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String verificationId;
    private EditText otpInput;
    private Button btnConfirm, btnResend, btnBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_confirmation);

        otpInput = findViewById(R.id.editTextOtp);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnResend = findViewById(R.id.btnResend);
        btnBack = findViewById(R.id.btnBack);

        // Load phone number and verification ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("OTP_PREFS", MODE_PRIVATE);
        String phoneNumber = prefs.getString("phoneNumber", null);
        verificationId = prefs.getString("verificationId", null);

        if (phoneNumber == null || verificationId == null) {
            Toast.makeText(this, "No phone number found. Please retry.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Confirm OTP input and verify
        btnConfirm.setOnClickListener(v -> {
            String code = otpInput.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            auth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Verification successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(OtpConfirmation.this, UserDashboardAct.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("OTP", "Verification failed", e);
                    Toast.makeText(this, "Invalid OTP. Try again.", Toast.LENGTH_SHORT).show();
                });
        });

        // Resend OTP with callback handling
        btnResend.setOnClickListener(v -> {
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) { }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(OtpConfirmation.this, "Resend failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(String newVerificationId, PhoneAuthProvider.ForceResendingToken token) {
                        verificationId = newVerificationId;
                        getSharedPreferences("OTP_PREFS", MODE_PRIVATE)
                            .edit()
                            .putString("verificationId", newVerificationId)
                            .apply();
                        Toast.makeText(OtpConfirmation.this, "OTP resent.", Toast.LENGTH_SHORT).show();
                    }
                }).build();

            PhoneAuthProvider.verifyPhoneNumber(options);
        });

        // Go back to previous screen
        btnBack.setOnClickListener(v -> finish());
    }
}
