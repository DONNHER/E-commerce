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

public class addAddress extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth myAuth = FirebaseAuth.getInstance();
    private tempStorage temp = tempStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_address);

        EditText streetET = findViewById(R.id.pass);
        EditText brngyET = findViewById(R.id.pass2);
        EditText cityET = findViewById(R.id.pass3);
        EditText provET = findViewById(R.id.pass4);
        EditText codeET = findViewById(R.id.pass5);
        CheckBox homeCB = findViewById(R.id.home);
        CheckBox workCB = findViewById(R.id.work);
        Button submitBtn = findViewById(R.id.buttonSignUp);
        Button back = findViewById(R.id.back2);

        AtomicReference<String> type = new AtomicReference<>("");

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

        back.setOnClickListener(view -> finish());

        ArrayList<EditText> editTexts = new ArrayList<>(List.of(streetET, brngyET, cityET, provET, codeET));

        submitBtn.setOnClickListener(v -> {
            String street = streetET.getText().toString().trim();
            String brngy = brngyET.getText().toString().trim();
            String city = cityET.getText().toString().trim();
            String prov = provET.getText().toString().trim();
            String code = codeET.getText().toString().trim();

            // Clear all previous errors
            for (EditText editText : editTexts) {
                editText.setError(null);
            }

            // Validate fields
            boolean proceed = true;
            for (EditText editText : editTexts) {
                String text = editText.getText().toString().trim();
                if (text.isEmpty()) {
                    editText.setError("Required");
                    proceed = false;
                } else if (!text.matches("[a-zA-Z0-9\\s]+")) {
                    editText.setError("No special characters allowed");
                    proceed = false;
                }
            }

            // Validate checkbox
            if (!homeCB.isChecked() && !workCB.isChecked()) {
                homeCB.setError("Required");
                workCB.setError("Required");
                proceed = false;
            }

            if (!proceed) return;

            address userAddress = new address(street, brngy, city, prov, code, type.get());
            temp.getAddressList().add(userAddress);

            Map<String, Object> data = new HashMap<>();
            data.put("street", street);
            data.put("baranggay", brngy);
            data.put("city", city);
            data.put("province", prov);
            data.put("code", code);
            data.put("name", type.get());

            // Use 'code' as a unique document ID to prevent duplicates
            db.collection("users")
                    .document(temp.getLoggedin())
                    .collection("address")
                    .document(street+","+brngy+","+city)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Log.d("Address", "Address with code " + code + " already exists");
                        } else {
                            db.collection("users")
                                    .document(temp.getLoggedin())
                                    .collection("address")
                                    .document(street+","+brngy+","+city)
                                    .set(data)
                                    .addOnSuccessListener(unused -> {
                                        Log.d("Address", "Address added: " + data);
                                        addAddress.this.finish();
                                    })
                                    .addOnFailureListener(e -> Log.e("Address", "Error adding address", e));
                        }
                    });
        });
        }
}
