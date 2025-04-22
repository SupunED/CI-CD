package com.example.test.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.test.BuildConfig;
import com.example.test.databinding.FragmentGalleryBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;

    private static final String API_KEY = BuildConfig.AIR_QUALITY_API_KEY;

    private static final String API_HOST = "air-quality-by-api-ninjas.p.rapidapi.com";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.aqiButton.setOnClickListener(v -> fetchAQIData());

        return root;
    }

    private void fetchAQIData() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://air-quality-by-api-ninjas.p.rapidapi.com/v1/airquality?city=Colombo")
                .get()
                .addHeader("x-rapidapi-key", API_KEY)
                .addHeader("x-rapidapi-host", API_HOST)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("AQI_API", "Network error", e);
                requireActivity().runOnUiThread(() ->
                        binding.aqiOutputField.setText("Network error. Please try again."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    Log.d("AQI_API", jsonData);

                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        JSONObject pm25 = jsonObject.getJSONObject("PM2.5");
                        double concentration = pm25.getDouble("concentration");
                        int aqi = pm25.getInt("aqi");

                        String aqiMeaning;
                        if (aqi <= 30) {
                            aqiMeaning = "Good";
                        } else if (aqi <= 60) {
                            aqiMeaning = "Satisfactory";
                        } else if (aqi <= 90) {
                            aqiMeaning = "Moderate";
                        } else if (aqi <= 120) {
                            aqiMeaning = "Poor";
                        } else if (aqi <= 250) {
                            aqiMeaning = "Very Poor";
                        } else {
                            aqiMeaning = "Severe";
                        }

                        String result = String.format(
                                "PM2.5: %.2f µg/m³\nAQI: %d (%s)",
                                concentration, aqi, aqiMeaning
                        );

                        requireActivity().runOnUiThread(() ->
                                binding.aqiOutputField.setText(result));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        requireActivity().runOnUiThread(() ->
                                binding.aqiOutputField.setText("Error parsing AQI data."));
                    }
                } else {
                    requireActivity().runOnUiThread(() ->
                            binding.aqiOutputField.setText("API error: " + response.code()));
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
