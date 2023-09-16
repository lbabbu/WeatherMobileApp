package ch.supsi.dti.isin.meteoapp.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import ch.supsi.dti.isin.meteoapp.R;
import ch.supsi.dti.isin.meteoapp.activities.DetailActivity;
import ch.supsi.dti.isin.meteoapp.http.OpenWeatherAPIService;
import ch.supsi.dti.isin.meteoapp.http.WeatherResponse;
import ch.supsi.dti.isin.meteoapp.model.LocationsHolder;
import ch.supsi.dti.isin.meteoapp.model.Location;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListFragment extends Fragment {
    private RecyclerView mLocationRecyclerView;
    private LocationAdapter mAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mLocationRecyclerView = view.findViewById(R.id.recycler_view);
        mLocationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        LocationsHolder locationsHolder = LocationsHolder.get(getActivity());
        locationsHolder.addLocationsLoadedCallback(new LocationsHolder.LocationsLoadedCallback() {
            @Override
            public void onLocationsLoaded(List<Location> locations) {
                mAdapter = new LocationAdapter(locations);
                mLocationRecyclerView.setAdapter(mAdapter);
                updateUI();
            }
        });

        return view;
    }


    @SuppressLint("NotifyDataSetChanged")
    public void updateUI() {
        List<Location> locations = LocationsHolder.get(getActivity()).getLocations();

        if (mAdapter == null) {
            mAdapter = new LocationAdapter(locations);
            mLocationRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setLocations(locations);
            mAdapter.notifyDataSetChanged();
        }
    }


    // Menu

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_list, menu);

    }


    private void getWeatherByCityName(String cityName, OnWeatherResponseListener listener) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        OpenWeatherAPIService service = retrofit.create(OpenWeatherAPIService.class);
        Call<WeatherResponse> weatherRequest = service.getWeatherByCity(cityName, "8ee77de7c0d74ad71c1aa7e069710ff7");

        weatherRequest.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();

                    listener.onResponse(weatherResponse);
                } else {
                    listener.onError();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                listener.onError();
            }
        });
    }

    interface OnWeatherResponseListener {
        void onResponse(WeatherResponse weatherResponse);

        void onError();
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:

                // create a new AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
                builder.setTitle("Add a location");

                // Set up the input
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(() -> {
                            String cityName = input.getText().toString();

                            // Create a new location and add it to the list
                            Location newLocation = new Location();
                            newLocation.setName(cityName);
                            // Add location to database
                            LocationsHolder.get(getActivity()).addLocationDB(newLocation);

                            // Fetch weather data for the new location
                            getWeatherByCityName(cityName, new OnWeatherResponseListener() {
                                @Override
                                public void onResponse(WeatherResponse weatherResponse) {
                                    if (weatherResponse != null) {
                                        newLocation.setmWeather(weatherResponse);
                                        updateUI();
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Notify the adapter of the addition
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    } else {
                                        // Remove the added location if the weather data is not available
                                        LocationsHolder.get(getActivity()).removeLocation(newLocation);
                                        showErrorToast("Unable to fetch weather data for " + cityName);
                                    }
                                }

                                @Override
                                public void onError() {
                                    // Remove the added location if an error occurs
                                    LocationsHolder.get(getActivity()).removeLocation(newLocation);
                                    showErrorToast("Error: Unable to fetch weather data for " + cityName);
                                }
                            });
                        }).start();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showErrorToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }


    // Holder

    private class LocationHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mNameTextView;
        private Location mLocation;

        public LocationHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item, parent, false));
            itemView.setOnClickListener(this);
            mNameTextView = itemView.findViewById(R.id.name);
        }

        @Override
        public void onClick(View view) {
            Intent intent = DetailActivity.newIntent(getActivity(), mLocation.getId());
            startActivity(intent);
        }

        public void bind(Location location) {
            mLocation = location;
            mNameTextView.setText(mLocation.getName());
        }
    }

    // Adapter

    private class LocationAdapter extends RecyclerView.Adapter<LocationHolder> {
        private List<Location> mLocations;

        public LocationAdapter(List<Location> locations) {
            mLocations = locations;
        }

        @Override
        public LocationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new LocationHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(LocationHolder holder, int position) {
            Location location = mLocations.get(position);
            holder.bind(location);
        }

        @Override
        public int getItemCount() {
            return mLocations.size();
        }

        public void setLocations(List<Location> locations) {
            mLocations = locations;
        }
    }
}