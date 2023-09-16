package ch.supsi.dti.isin.meteoapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.UUID;

import ch.supsi.dti.isin.meteoapp.R;
import ch.supsi.dti.isin.meteoapp.fragments.DetailLocationFragment;
import ch.supsi.dti.isin.meteoapp.model.Location;
import ch.supsi.dti.isin.meteoapp.model.LocationsHolder;


public class DetailActivity extends AppCompatActivity {

    private static final String EXTRA_LOCATION_ID = "ch.supsi.dti.isin.meteoapp.location_id";
    private static final String EXTRA_GPS_LATITUDE = "ch.supsi.dti.isin.meteoapp.gps_latitude";
    private static final String EXTRA_GPS_LONGITUDE = "ch.supsi.dti.isin.meteoapp.gps_longitude";

    public static Intent newIntent(Context packageContext, UUID locationId) {
        Intent intent = new Intent(packageContext, DetailActivity.class);
        intent.putExtra(EXTRA_LOCATION_ID, locationId);
        return intent;
    }


    public static Intent newIntent(Context packageContext, double latitude, double longitude) {
        Intent intent = new Intent(packageContext, DetailActivity.class);
        //search location by coordinates and set location id
        intent.putExtra(EXTRA_GPS_LATITUDE, latitude);
        intent.putExtra(EXTRA_GPS_LONGITUDE, longitude);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail_location);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container);

        if (getIntent().hasExtra(EXTRA_LOCATION_ID)) {
            UUID locationId = (UUID) getIntent().getSerializableExtra(EXTRA_LOCATION_ID);
            Location location = LocationsHolder.get(this).getLocation(locationId);

            if (location != null && location.getmWeather() != null) {
                double latitude = location.getmWeather().getCoord().getLat();
                double longitude = location.getmWeather().getCoord().getLon();

                if (fragment == null) {
                    fragment = DetailLocationFragment.newInstance(latitude, longitude);
                    fm.beginTransaction()
                            .add(R.id.container, fragment)
                            .commit();
                }
            } else {
                if(fragment == null) {
                    fragment = DetailLocationFragment.newInstance(location.getName());
                    fm.beginTransaction()
                            .add(R.id.container, fragment)
                            .commit();
                }

            }
        } else {
            double latitude = getIntent().getDoubleExtra(EXTRA_GPS_LATITUDE, 0);
            double longitude = getIntent().getDoubleExtra(EXTRA_GPS_LONGITUDE, 0);

            if (fragment == null) {
                fragment = DetailLocationFragment.newInstance(latitude, longitude);
                fm.beginTransaction()
                        .add(R.id.container, fragment)
                        .commit();
            }
        }
    }
}
