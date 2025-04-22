package com.example.test.ui.slideshow;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.test.BuildConfig;
import com.example.test.databinding.FragmentSlideshowBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;

    private static final String API_KEY = BuildConfig.CALORIE_API_KEY;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Optional: Observe ViewModel text
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), binding.textSlideshow::setText);

        // Handle API button click
        binding.apiCallButton.setOnClickListener(view -> {
            String food = binding.food.getText().toString().trim();
            if (!food.isEmpty()) {
                fetchNutritionData(food);
            } else {
                binding.facts.setText("Please enter a food item.");
            }
        });

        return root;
    }

    private void fetchNutritionData(String query) {
        OkHttpClient client = new OkHttpClient();

        String url = "https://api.calorieninjas.com/v1/nutrition?query=" + query.replace(" ", "%20");

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Api-Key", API_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("API_ERROR", "Network Request Failed", e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            binding.facts.setText("Network error. Please try again."));
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("API_RESPONSE", responseData);

                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONArray items = jsonObject.getJSONArray("items");

                        if (items.length() > 0) {
                            JSONObject foodItem = items.getJSONObject(0);

                            double calories = foodItem.getDouble("calories");
                            double fat = foodItem.getDouble("fat_total_g");
                            double carbs = foodItem.getDouble("carbohydrates_total_g");
                            double protein = foodItem.getDouble("protein_g");

                            String result = "Calories: " + calories + " kcal\n"
                                    + "Fat: " + fat + " g\n"
                                    + "Carbohydrates: " + carbs + " g\n"
                                    + "Protein: " + protein + " g";

                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() ->
                                        binding.facts.setText(result));
                            }
                        } else {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() ->
                                        binding.facts.setText("No nutrition data found."));
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() ->
                                    binding.facts.setText("Error parsing response."));
                        }
                    }

                } else {
                    Log.e("API_ERROR", "Unexpected response: " + response.code());
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() ->
                                binding.facts.setText("Failed to fetch data. Error code: " + response.code()));
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
