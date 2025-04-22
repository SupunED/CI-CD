package com.example.test.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.test.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Optional ViewModel text
        final TextView textView = binding.textView2;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // BMI Calculator Logic
        binding.bmiCalButton.setOnClickListener(view -> {
            String weightStr = binding.weightinKg.getText().toString().trim();
            String heightStr = binding.heightInMetres.getText().toString().trim();

            if (!weightStr.isEmpty() && !heightStr.isEmpty()) {
                try {
                    float weight = Float.parseFloat(weightStr);
                    float height = Float.parseFloat(heightStr);

                    if (height <= 0 || weight <= 0) {
                        showAlert("Invalid Input", "Please enter valid height and weight values.");
                        return;
                    }

                    float bmiValue = weight / (height * height);
                    String bmiCategory;
                    int textColor;

                    if (bmiValue < 18.5) {
                        bmiCategory = "Underweight";
                        textColor = Color.BLUE;
                    } else if (bmiValue <= 22.9) {
                        bmiCategory = "Normal";
                        textColor = Color.GREEN;
                    } else if (bmiValue <= 24.9) {
                        bmiCategory = "Risk to Overweight";
                        textColor = Color.YELLOW;
                    } else if (bmiValue <= 29.9) {
                        bmiCategory = "Overweight";
                        textColor = 0xFFFFA500; // Orange
                    } else {
                        bmiCategory = "Obese";
                        textColor = Color.RED;
                    }

                    String bmiResult = String.format("Your BMI is: %.2f\nCategory: %s", bmiValue, bmiCategory);

                    TextView styledMessage = new TextView(getContext());
                    styledMessage.setText(bmiResult);
                    styledMessage.setTextSize(18);
                    styledMessage.setPadding(50, 40, 50, 10);
                    styledMessage.setTextColor(textColor);
                    styledMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    new AlertDialog.Builder(requireContext())
                            .setTitle("BMI Result")
                            .setView(styledMessage)
                            .setPositiveButton("OK", null)
                            .show();

                } catch (NumberFormatException e) {
                    showAlert("Error", "Invalid input format. Please use numbers.");
                }
            } else {
                showAlert("Missing Input", "Please enter both height and weight.");
            }
        });

        return root;
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
