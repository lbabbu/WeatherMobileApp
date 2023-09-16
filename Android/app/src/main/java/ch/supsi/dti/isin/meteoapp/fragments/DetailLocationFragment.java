package ch.supsi.dti.isin.meteoapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import ch.supsi.dti.isin.meteoapp.R;
import ch.supsi.dti.isin.meteoapp.http.OpenWeatherAPIService;
import ch.supsi.dti.isin.meteoapp.http.WeatherResponse;
import ch.supsi.dti.isin.meteoapp.model.Location;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailLocationFragment extends Fragment {
    private static final String GPS_LATITUDE = "latitude";
    private static final String GPS_LONGINTUDE = "longitude";
    private static final String CITY_NAME = "city_name";
    private Location mLocation;

    private TextView cityName;
    private TextView temperature;
    private TextView description;
    private ImageView weatherIcon;

    public static DetailLocationFragment newInstance(double latitude, double longitude) {
        Bundle args = new Bundle();
        args.putSerializable(GPS_LATITUDE, latitude);
        args.putSerializable(GPS_LONGINTUDE, longitude);
        DetailLocationFragment fragment = new DetailLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public static DetailLocationFragment newInstance(String cityName) {
        Bundle args = new Bundle();
        args.putSerializable(CITY_NAME, cityName);

        DetailLocationFragment fragment = new DetailLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(GPS_LATITUDE) && getArguments().containsKey(GPS_LONGINTUDE)) {
            double latitude = (double) getArguments().getSerializable(GPS_LATITUDE);
            double longitude = (double) getArguments().getSerializable(GPS_LONGINTUDE);
            mLocation = new Location();
            try {
                getLocationFromGPS(latitude, longitude);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (getArguments().containsKey(CITY_NAME)) {
            String cityName = (String) getArguments().getSerializable(CITY_NAME);
            mLocation = new Location();
            try {
                getLocationFromName(cityName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "No location detected", Toast.LENGTH_SHORT).show();

        }

    }

    @SuppressLint("DefaultLocale")
    private String kelvinToCelsius(double kelvin) {
        //return String.format("%.1f", kelvin);
        return String.format("%.1f", kelvin - 273.15);
    }


    private void getLocationFromGPS(double lat, double lon) throws IOException {
        if (lat == 0 && lon == 0) {
            //Toast.makeText(this, "No location detected", Toast.LENGTH_SHORT).show();
            return;
        }
        AtomicReference<String> city = new AtomicReference<>("");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/") // call to openweather api
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //create interface
        OpenWeatherAPIService service = retrofit.create(OpenWeatherAPIService.class);
        Call<WeatherResponse> weatherRequest = service.getWeatherByCoordinates(lat, lon, "8ee77de7c0d74ad71c1aa7e069710ff7");
        //async call
        DetailLocationFragment context = this;
        weatherRequest.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                Log.i("Response", response.toString());
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    //do something with weatherResponse
                    //log the response
                    weatherResponse.setReady(true);
                    mLocation.setmWeather(weatherResponse);
                    mLocation.setName(weatherResponse.getName());
                    Log.i("Icon", weatherResponse.getWeather().get(0).getIcon());
                    //alert dialog with json
                    updateUI(); // Aggiungi questa riga
                } else {
                    Log.e("Error", response.errorBody().toString());
                }
            }

            @SuppressLint({"DiscouragedApi", "SetTextI18n"})
            private void updateUI() {
                if (mLocation.getmWeather() != null && mLocation.getmWeather().isReady()) {
                    cityName.setText(mLocation.getName());
                    temperature.setText(kelvinToCelsius(mLocation.getmWeather().getMain().getTemp()) + " °C");
                    String desc=mLocation.getmWeather().getWeather().get(0).getDescription();
                    String[] words = desc.split("\\s");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < words.length; i++) {
                        sb.append(Character.toUpperCase(words[i].charAt(0)));
                        sb.append(words[i].substring(1));
                        sb.append(" ");
                    }
                    description.setText(sb.toString());
                    weatherIcon.setImageResource(getResources().getIdentifier("drawable/i" + mLocation.getmWeather().getWeather().get(0).getIcon(), null, getActivity().getPackageName()));
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("Error", t.getMessage());

            }
        });
    }

    private void getLocationFromName(String cityNameInput) throws IOException {
        if (cityNameInput == null || cityNameInput.isEmpty()) {
            return;
        }
        AtomicReference<String> city = new AtomicReference<>("");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/") // call to openweather api
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //create interface
        OpenWeatherAPIService service = retrofit.create(OpenWeatherAPIService.class);
        Call<WeatherResponse> weatherRequest = service.getWeatherByCity(cityNameInput, "8ee77de7c0d74ad71c1aa7e069710ff7");
        //async call
        DetailLocationFragment context = this;
        weatherRequest.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                Log.i("Response", response.toString());
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    //do something with weatherResponse
                    //log the response
                    weatherResponse.setReady(true);
                    mLocation.setmWeather(weatherResponse);
                    mLocation.setName(cityNameInput);
                    //alert dialog with json
                    updateUI(); // Aggiungi questa riga
                } else {
                    Log.e("Error", response.errorBody().toString());
                }
            }

            @SuppressLint({"DiscouragedApi", "SetTextI18n"})
            private void updateUI() {
                if (mLocation.getmWeather() != null && mLocation.getmWeather().isReady()) {
                    cityName.setText(mLocation.getName());
                    temperature.setText(kelvinToCelsius(mLocation.getmWeather().getMain().getTemp()) + " °C");
                    String desc=mLocation.getmWeather().getWeather().get(0).getDescription();
                    String[] words = desc.split("\\s");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < words.length; i++) {
                        sb.append(Character.toUpperCase(words[i].charAt(0)));
                        sb.append(words[i].substring(1));
                        sb.append(" ");
                    }
                    description.setText(sb.toString());
                    weatherIcon.setImageResource(getResources().getIdentifier("drawable/i" + mLocation.getmWeather().getWeather().get(0).getIcon(), null, getActivity().getPackageName()));
                }
            }


            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("Error", t.getMessage());

            }
        });

    }


    private Handler handler = new Handler();

    private Runnable updateUIRunnable = new Runnable() {
        @SuppressLint({"DiscouragedApi", "SetTextI18n"})
        @Override
        public void run() {
            if (mLocation.getmWeather() != null && mLocation.getmWeather().isReady()) {
                cityName.setText(mLocation.getName());
                temperature.setText(kelvinToCelsius(mLocation.getmWeather().getMain().getTemp()) + " °C");
                description.setText(mLocation.getmWeather().getWeather().get(0).getDescription());
                weatherIcon.setImageResource(getResources().getIdentifier("drawable/i" + mLocation.getmWeather().getWeather().get(0).getIcon(), null, getActivity().getPackageName()));

            } else {
                handler.postDelayed(this, 500);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail_location, container, false);

        if (mLocation == null) {
            return v;
        }

        cityName = v.findViewById(R.id.cityNameTextBox);
        temperature = v.findViewById(R.id.temperatureText);
        description = v.findViewById(R.id.weatherDescription);
        weatherIcon = v.findViewById(R.id.weatherIcon);
        return v;
    }
}

