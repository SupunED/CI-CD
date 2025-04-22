package com.example.test;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmergencyContactsFragment extends Fragment {

    private TextView bloodTypeValue, allergiesValue, medicalHistoryValue, emergencyContactValue;
    private ImageView editBloodType, editAllergies, editMedicalHistory, editEmergencyContact;
    private Button applyChangesButton;

    private DatabaseReference userRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency_contacts, container, false);

        // Linking views
        bloodTypeValue = view.findViewById(R.id.bloodTypeValue);
        allergiesValue = view.findViewById(R.id.allergiesValue);
        medicalHistoryValue = view.findViewById(R.id.medicalHistoryValue);
        emergencyContactValue = view.findViewById(R.id.emergencyContactValue);

        editBloodType = view.findViewById(R.id.editBloodType);
        editAllergies = view.findViewById(R.id.editAllergies);
        editMedicalHistory = view.findViewById(R.id.editMedicalHistory);
        editEmergencyContact = view.findViewById(R.id.editEmergencyContact);
        applyChangesButton = view.findViewById(R.id.applyChangesButton); // ensure this ID exists

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

            // Retrieve emergency data
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    bloodTypeValue.setText(snapshot.child("bloodType").getValue(String.class));
                    allergiesValue.setText(snapshot.child("allergies").getValue(String.class));
                    medicalHistoryValue.setText(snapshot.child("medicalHistory").getValue(String.class));
                    emergencyContactValue.setText(snapshot.child("emergencyContact").getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to load emergency info", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Hook up edit actions
        editField(editBloodType, bloodTypeValue, "Blood Type", InputType.TYPE_CLASS_TEXT);
        editField(editAllergies, allergiesValue, "Allergies", InputType.TYPE_CLASS_TEXT);
        editField(editMedicalHistory, medicalHistoryValue, "Medical History", InputType.TYPE_CLASS_TEXT);
        editField(editEmergencyContact, emergencyContactValue, "Emergency Contact", InputType.TYPE_CLASS_PHONE);

        // Save to Firebase on button click
        applyChangesButton.setOnClickListener(v -> {
            if (userRef != null) {
                userRef.child("bloodType").setValue(bloodTypeValue.getText().toString());
                userRef.child("allergies").setValue(allergiesValue.getText().toString());
                userRef.child("medicalHistory").setValue(medicalHistoryValue.getText().toString());
                userRef.child("emergencyContact").setValue(emergencyContactValue.getText().toString());

                Toast.makeText(getContext(), "Changes Applied", Toast.LENGTH_SHORT).show();
            }
        });

        // Emergency number click listeners
        setupDialer(view.findViewById(R.id.emgFireAndAmbulance), "110");
        setupDialer(view.findViewById(R.id.emgAmbulance), "1990");
        setupDialer(view.findViewById(R.id.police), "119");

        return view;
    }

    private void setupDialer(TextView textView, String phoneNumber) {
        if (textView != null) {
            textView.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            });
        }
    }

    private void editField(ImageView editIcon, TextView valueView, String fieldName, int inputType) {
        editIcon.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Edit " + fieldName);

            final EditText input = new EditText(requireContext());
            input.setInputType(inputType);

            String currentText = valueView.getText().toString().trim();
            if (!currentText.isEmpty()) {
                input.setText(currentText);
                input.setSelection(currentText.length());
            }

            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                String newValue = input.getText().toString().trim();
                valueView.setText(newValue);
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        });
    }
}
