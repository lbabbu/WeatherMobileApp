package ch.supsi.dti.isin.meteoapp.Repositories;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Build;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.UUID;

import ch.supsi.dti.isin.meteoapp.model.Location;
import ch.supsi.dti.isin.meteoapp.model.LocationDB;
import ch.supsi.dti.isin.meteoapp.model.LocationDao;

public class LocationRepository {
    private LocationDao locationDao;
    private List<Location> allLocations;

    public LocationRepository(Application application) {
        LocationDB db = LocationDB.getInstance(application);
        locationDao = db.locationDao();
        allLocations = locationDao.getLocations();
    }

    public void insert(Location location) {
        new InsertLocationAsyncTask(locationDao).execute(location);
    }

    public void update(Location location) {
        new UpdateLocationAsyncTask(locationDao).execute(location);
    }

    public void delete(Location location) {
        new DeleteLocationAsyncTask(locationDao).execute(location);
    }

    public List<Location> getAllLocations() {
        return allLocations;
    }

    public Location getLocationById(UUID id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return locationDao.getLocations().stream().filter(l -> l.getId().equals(id)).findFirst().orElse(null);
        }
        return null;
    }

    private static class InsertLocationAsyncTask extends AsyncTask<Location, Void, Void> {
        private LocationDao locationDao;

        private InsertLocationAsyncTask(LocationDao locationDao) {
            this.locationDao = locationDao;
        }

        @Override
        protected Void doInBackground(Location... locations) {
            locationDao.insertLocation(locations[0]);
            return null;
        }
    }

    private static class UpdateLocationAsyncTask extends AsyncTask<Location, Void, Void> {
        private LocationDao locationDao;

        private UpdateLocationAsyncTask(LocationDao locationDao) {
            this.locationDao = locationDao;
        }

        @Override
        protected Void doInBackground(Location... locations) {
            locationDao.updateLocation(locations[0]);
            return null;
        }
    }

    private static class DeleteLocationAsyncTask extends AsyncTask<Location, Void, Void> {
        private LocationDao locationDao;

        private DeleteLocationAsyncTask(LocationDao locationDao) {
            this.locationDao = locationDao;
        }

        @Override
        protected Void doInBackground(Location... locations) {
            locationDao.deleteLocation(locations[0]);
            return null;
        }
    }
}