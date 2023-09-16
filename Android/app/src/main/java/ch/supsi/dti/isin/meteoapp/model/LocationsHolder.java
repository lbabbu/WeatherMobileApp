package ch.supsi.dti.isin.meteoapp.model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

public class LocationsHolder {
    private static LocationsHolder sLocationsHolder;
    private LocationDB db;
    private Executor mExecutor;
    private List<Location> mLocations;
    private List<LocationsLoadedCallback> mCallbacks;

    public interface LocationsLoadedCallback {
        void onLocationsLoaded(List<Location> locations);
    }

    private LocationsHolder(Context context) {
        db = LocationDB.getInstance(context);
        mExecutor = Executors.newSingleThreadExecutor();
        mLocations = new ArrayList<>();
        mCallbacks = new ArrayList<>();
        fetchLocationsFromDatabase();
    }

    public static LocationsHolder get(Context context) {
        if (sLocationsHolder == null) {
            sLocationsHolder = new LocationsHolder(context);
        }
        return sLocationsHolder;
    }

    public void addLocationsLoadedCallback(LocationsLoadedCallback callback) {
        if (mLocations.isEmpty()) {
            mCallbacks.add(callback);
        } else {
            callback.onLocationsLoaded(mLocations);
        }
    }

    private void fetchLocationsFromDatabase() {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<Location> locations = db.locationDao().getLocations();
                if (locations.isEmpty()) {
                    //createRealLocations();
                } else {
                    mLocations.addAll(locations);
                    for (LocationsLoadedCallback callback : mCallbacks) {
                        callback.onLocationsLoaded(mLocations);
                    }
                    mCallbacks.clear();
                }
            }
        });
    }

    public static GPSCoordinates getLocalLocation(Context context, final Location location) {
        GPSCoordinates gpsCoordinates = new GPSCoordinates(0, 0);
        //smartlocation get location
        LocationParams.Builder builder = new LocationParams.Builder();
        builder.setAccuracy(LocationAccuracy.HIGH);
        builder.setDistance(0);
        builder.setInterval(1000);
        SmartLocation.with(context).location().continuous().config(builder.build())
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(android.location.Location location) {
                        Log.d("loc", "onLocationUpdated: " + location.getLatitude() + " " + location.getLongitude());
                        gpsCoordinates.setLatitude(location.getLatitude());
                        gpsCoordinates.setLongitude(location.getLongitude());

                    }
                });
        return gpsCoordinates;
    }

    public GPSCoordinates getCurrentLocation(Context context) {
        final GPSCoordinates[] gpsCoordinates = new GPSCoordinates[1];
        final CountDownLatch latch = new CountDownLatch(1);

        LocationParams.Builder builder = new LocationParams.Builder();
        builder.setAccuracy(LocationAccuracy.HIGH);
        builder.setDistance(0);
        builder.setInterval(1000);
        SmartLocation.with(context).location().oneFix().config(builder.build())
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(android.location.Location location) {
                        Log.d("loc", "onLocationUpdated: " + location.getLatitude() + " " + location.getLongitude());
                        gpsCoordinates[0] = new GPSCoordinates(location.getLatitude(), location.getLongitude());
                        latch.countDown();
                    }
                });

        try {
            latch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return gpsCoordinates[0];
    }

    public void addLocationDB(Location location) {
        mExecutor.execute(() -> db.locationDao().insertLocation(location));
        mLocations.add(location);
    }

    public void removeLocation(Location location) {
        mExecutor.execute(() -> db.locationDao().deleteLocation(location));
        mLocations.remove(location);
    }

    public List<Location> getLocations() {
        return mLocations;
    }

    public Location getLocation(UUID id) {
        for (Location location : mLocations) {
            if (location.getId().equals(id))
                return location;
        }

        return null;
    }
}




/*    private void createRealLocations() {
        mExecutor.execute(() -> {
            List<Location> dbLocations = db.locationDao().getLocations();
            if (dbLocations.isEmpty()) {
                String[] cityNames = {
                        "London", "Milan", "New York", "Paris", "Tokyo", "Sydney", "Moscow", "Mumbai", "Rio de Janeiro", "Toronto"
                };

                double[][] cityCoordinates = {
                        {51.5074, -0.1278},
                        {45.4642, 9.1900},
                        {40.7128, -74.0060},
                        {48.8566, 2.3522},
                        {35.6895, 139.6917},
                        {-33.8688, 151.2093},
                        {55.7558, 37.6173},
                        {19.0760, 72.8777},
                        {-22.9068, -43.1729},
                        {43.6532, -79.3832}
                };

                for (int i = 0; i < cityNames.length; i++) {
                    Location location = new Location();
                    WeatherResponse weather = new WeatherResponse();
                    WeatherResponse.Coord coord = new WeatherResponse.Coord();
                    coord.setLat(cityCoordinates[i][0]);
                    coord.setLon(cityCoordinates[i][1]);
                    weather.setCoord(coord);

                    location.setName(cityNames[i]);
                    location.setmWeather(weather);

                    // Insert location in database
                    db.locationDao().insertLocation(location);

                    mLocations.add(location);
                }
            } else {
                mLocations.addAll(dbLocations);
            }
        });
    }*/













































/*
package ch.supsi.dti.isin.meteoapp.model;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ch.supsi.dti.isin.meteoapp.fragments.ListFragment;
import ch.supsi.dti.isin.meteoapp.http.OpenWeatherAPIService;
import ch.supsi.dti.isin.meteoapp.http.WeatherResponse;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationsHolder {

    private static LocationsHolder sLocationsHolder;
    private List<Location> mLocations;

    private ListFragment listFragment;

    private LocationDB db;

    private Executor mExecutor = Executors.newSingleThreadExecutor();

    private LocationsHolder(Context context) {
        mLocations = new ArrayList<>();
        db = LocationDB.getInstance(context);
        createRealLocations();
    }


*/
/*    private LocationsHolder(Context context) {
        mLocations = new ArrayList<>();

        db = LocationDB.getInstance(context);

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                createRealLocations();
            }
        });
    }*//*


    public void addLocationDB(Location location) {
        mExecutor.execute(() -> db.locationDao().insertLocation(location));
        mLocations.add(location);
    }




    public Location getLocation(UUID id) {
        for (Location location : mLocations) {
            if (location.getId().equals(id))
                return location;
        }

        return null;
    }

*/
/*
    public void addLocationDB(Location location) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                db.locationDao().insertLocation(location);
            }
        });
        listFragment.updateUI();

    }*//*


    public static LocationsHolder get(Context context) {
        if (sLocationsHolder == null)
            sLocationsHolder = new LocationsHolder(context)
                    ;

        return sLocationsHolder;
    }



    private void createRealLocations() {
        String[] cityNames = {
                "London", "Milan", "New York", "Paris", "Tokyo", "Sydney", "Moscow", "Mumbai", "Rio de Janeiro", "Toronto"
        };

        double[][] cityCoordinates = {
                {51.5074, -0.1278},
                {45.4642, 9.1900},
                {40.7128, -74.0060},
                {48.8566, 2.3522},
                {35.6895, 139.6917},
                {-33.8688, 151.2093},
                {55.7558, 37.6173},
                {19.0760, 72.8777},
                {-22.9068, -43.1729},
                {43.6532, -79.3832}
        };

        for (int i = 0; i < cityNames.length; i++) {
            Location location = new Location();
            WeatherResponse weather = new WeatherResponse();
            WeatherResponse.Coord coord = new WeatherResponse.Coord();
            coord.setLat(cityCoordinates[i][0]);
            coord.setLon(cityCoordinates[i][1]);
            weather.setCoord(coord);

            location.setName(cityNames[i]);
            location.setmWeather(weather);
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    // Insert location in database
                    db.locationDao().insertLocation(location);
                }
            });

            mLocations.add(location);
        }
    }


    public static GPSCoordinates getLocalLocation(Context context, final Location location) {
        GPSCoordinates gpsCoordinates = new GPSCoordinates(0, 0);
        //smartlocation get location
        LocationParams.Builder builder = new LocationParams.Builder();
        builder.setAccuracy(LocationAccuracy.HIGH);
        builder.setDistance(0);
        builder.setInterval(1000);
        SmartLocation.with(context).location().continuous().config(builder.build())
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(android.location.Location location) {
                        Log.d(TAG, "onLocationUpdated: " + location.getLatitude() + " " + location.getLongitude());
                        gpsCoordinates.setLatitude(location.getLatitude());
                        gpsCoordinates.setLongitude(location.getLongitude());

                    }
                });
        return gpsCoordinates;
    }


    public List<Location> getLocations() {
        return mLocations;
    }

    public void addLocation(Location location) {
        mLocations.add(location);
    }

    public void removeLocation(Location newLocation) {
        mLocations.remove(newLocation);
    }
}
*/
