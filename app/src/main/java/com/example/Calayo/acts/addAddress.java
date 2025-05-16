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
 * This activity lets the user add a new address (Home or Work).
 * It checks if the info is correct and saves it to Firebase.
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

        // Firebase and local storage setup
        db = FirebaseFirestore.getInstance();
        myAuth = FirebaseAuth.getInstance();
        temp = tempStorage.getInstance();

        // Get the input fields
        EditText streetET = findViewById(R.id.pass);
        EditText brngyET = findViewById(R.id.pass2);
        EditText cityET = findViewById(R.id.pass3);
        EditText provET = findViewById(R.id.pass4);
        EditText codeET = findViewById(R.id.pass5);

        // Checkboxes for choosing Home or Work
        CheckBox homeCB = findViewById(R.id.home);
        CheckBox workCB = findViewById(R.id.work);

        // Buttons for submit and back
        Button submitBtn = findViewById(R.id.buttonSignUp);
        Button backBtn = findViewById(R.id.back2);

        // This holds the selected address type
        AtomicReference<String> type = new AtomicReference<>("");

        // If Home is checked, uncheck Work
        homeCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                type.set("Home");
                workCB.setChecked(false);
            }
        });

        // If Work is checked, uncheck Home
        workCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                type.set("Work");
                homeCB.setChecked(false);
            }
        });

        // Go back when back button is clicked
        backBtn.setOnClickListener(view -> finish());

        // List of all EditText fields
        ArrayList<EditText> editTexts = new ArrayList<>(List.of(streetET, brngyET, cityET, provET, codeET));

        // When user clicks Submit
        submitBtn.setOnClickListener(v -> {
            // Get text from all input fields
            String street = streetET.getText().toString().trim();
            String brngy = brngyET.getText().toString().trim();
            String city = cityET.getText().toString().trim();
            String prov = provET.getText().toString().trim();
            String code = codeET.getText().toString().trim();

            // Clear all previous error messages
            for (EditText editText : editTexts) {
                editText.setError(null);
            }

            // Check if input is valid
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

            // Check if Home or Work was selected
            if (!homeCB.isChecked() && !workCB.isChecked()) {
                Toast.makeText(this, "Please select Home or Work", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            // If something is wrong, stop here
            if (!isValid) {
                Log.w(TAG, "Validation failed, user input is invalid");
                return;
            }

            // Check if user is logged in
            if (temp.getLoggedin() == null || temp.getLoggedin().isEmpty()) {
                Log.e(TAG, "User not logged in");
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create the address object
            address userAddress = new address(street, brngy, city, prov, code, type.get());

            // Save to local storage
            temp.getAddressList().add(userAddress);

            // Prepare data to be uploaded to Firestore
            Map<String, Object> data = new HashMap<>();
            data.put("street", street);
            data.put("baranggay", brngy);
            data.put("city", city);
            data.put("province", prov);
            data.put("code", code);
            data.put("name", type.get());

            // Create a unique document ID using address parts
            String docId = street + "," + brngy + "," + city;

            // Check if address already exists in Firestore
            db.collection("users")
                    .document(temp.getLoggedin())
                    .collection("address")
                    .document(docId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // If address is already there, show message
                            Log.d(TAG, "Address already exists: " + docId);
                            Toast.makeText(this, "Address already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            // Save the new address in Firestore
                            db.collection("users")
                                    .document(temp.getLoggedin())
                                    .collection("address")
                                    .document(docId)
                                    .set(data)
                                    .addOnSuccessListener(unused -> {
                                        Log.d(TAG, "Address added: " + data);
                                        Toast.makeText(this, "Address added successfully", Toast.LENGTH_SHORT).show();
                                        finish(); // Go back after adding
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error adding address", e);
                                        Toast.makeText(this, "Failed to add address", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        // If there’s a problem checking address
                        Log.e(TAG, "Error checking address existence", e);
                        Toast.makeText(this, "Error validating address", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
