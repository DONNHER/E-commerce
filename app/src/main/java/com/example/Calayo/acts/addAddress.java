package com.example.Calayo.acts;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Calayo.R;
import com.example.Calayo.entities.address;
import com.example.Calayo.helper.tempStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Activity to add a new address for a user.
 * Validates inputs and submits data to Firebase Firestore.
 */
public class addAddress extends AppCompatActivity {

    private static final String TAG = "addAddress";

    private FirebaseFirestore db;
    private FirebaseAuth myAuth;
    private tempStorage temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_address);

        db = FirebaseFirestore.getInstance();
        myAuth = FirebaseAuth.getInstance();
        temp = tempStorage.getInstance();

        EditText streetET = findViewById(R.id.pass);
        EditText brngyET = findViewById(R.id.pass2);
        EditText cityET = findViewById(R.id.pass3);
        EditText provET = findViewById(R.id.pass4);
        EditText codeET = findViewById(R.id.pass5);

        CheckBox homeCB = findViewById(R.id.home);
        CheckBox workCB = findViewById(R.id.work);
        Button submitBtn = findViewById(R.id.buttonSignUp);
        Button backBtn = findViewById(R.id.back2);

        AtomicReference<String> type = new AtomicReference<>("");

        // Handle checkbox exclusivity
        homeCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                type.set("Home");
                workCB.setChecked(false);
            }
        });

        workCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                type.set("Work");
                homeCB.setChecked(false);
            }
        });

        backBtn.setOnClickListener(view -> finish());

        ArrayList<EditText> editTexts = new ArrayList<>(List.of(streetET, brngyET, cityET, provET, codeET));

        submitBtn.setOnClickListener(v -> {
            // Retrieve and sanitize inputs
            String street = streetET.getText().toString().trim();
            String brngy = brngyET.getText().toString().trim();
            String city = cityET.getText().toString().trim();
            String prov = provET.getText().toString().trim();
            String code = codeET.getText().toString().trim();

            // Clear all previous input errors
            for (EditText editText : editTexts) {
                editText.setError(null);
            }

            // Validate inputs
            boolean isValid = true;
            for (EditText et : editTexts) {
                String input = et.getText().toString().trim();
                if (input.isEmpty()) {
                    et.setError("This field is required");
                    isValid = false;
                } else if (!input.matches("[a-zA-Z0-9\\s]+")) {
                    et.setError("Only alphanumeric and spaces allowed");
                    isValid = false;
                }
            }

            if (!homeCB.isChecked() && !workCB.isChecked()) {
                Toast.makeText(this, "Please select Home or Work", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            if (!isValid) {
                Log.w(TAG, "Validation failed, user input is invalid");
                return;
            }

            if (temp.getLoggedin() == null || temp.getLoggedin().isEmpty()) {
                Log.e(TAG, "User not logged in");
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create address object
            address userAddress = new address(street, brngy, city, prov, code, type.get());
            temp.getAddressList().add(userAddress);

            // Prepare data for Firestore
            Map<String, Object> data = new HashMap<>();
            data.put("street", street);
            data.put("baranggay", brngy);
            data.put("city", city);
            data.put("province", prov);
            data.put("code", code);
            data.put("name", type.get());

            String docId = street + "," + brngy + "," + city;

            // Firestore: Check if address exists before writing
            db.collection("users")
                    .document(temp.getLoggedin())
                    .collection("address")
                    .document(docId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Log.d(TAG, "Address already exists: " + docId);
                            Toast.makeText(this, "Address already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            db.collection("users")
                                    .document(temp.getLoggedin())
                                    .collection("address")
                                    .document(docId)
                                    .set(data)
                                    .addOnSuccessListener(unused -> {
                                        Log.d(TAG, "Address added: " + data);
                                        Toast.makeText(this, "Address added successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error adding address", e);
                                        Toast.makeText(this, "Failed to add address", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error checking address existence", e);
                        Toast.makeText(this, "Error validating address", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
