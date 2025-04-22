package com.example.test.ui.settings;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.test.databinding.FragmentSettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            usersRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

            // Load user data
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String username = snapshot.child("username").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String birthday = snapshot.child("birthday").getValue(String.class);

                        binding.editTextText3.setText(username);
                        binding.editTextTextEmailAddress3.setText(email);
                        binding.editTextTextPassword2.setText(birthday);

                        // Make email non-editable
                        binding.editTextTextEmailAddress3.setFocusable(false);
                        binding.editTextTextEmailAddress3.setClickable(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });

            // Save Changes
            binding.button5.setOnClickListener(v -> {
                String updatedUsername = binding.editTextText3.getText().toString().trim();
                String updatedBirthday = binding.editTextTextPassword2.getText().toString().trim();

                usersRef.child("username").setValue(updatedUsername);
                usersRef.child("birthday").setValue(updatedBirthday);

                Toast.makeText(getContext(), "Changes saved successfully", Toast.LENGTH_SHORT).show();
            });

            // Delete Account
            binding.buttonDeleteAccount.setOnClickListener(v -> {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Account")
                        .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Remove from Realtime Database
                            usersRef.removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Remove from Firebase Authentication
                                    currentUser.delete().addOnCompleteListener(deleteTask -> {
                                        if (deleteTask.isSuccessful()) {
                                            Toast.makeText(getContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                            mAuth.signOut();
                                            requireActivity().finish(); // Optionally navigate to login screen
                                        } else {
                                            Toast.makeText(getContext(), "Failed to delete account: " + deleteTask.getException(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(getContext(), "Failed to delete user data", Toast.LENGTH_LONG).show();
                                }
                            });
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
