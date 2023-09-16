package ch.supsi.dti.isin.meteoapp.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Location.class}, version = 1, exportSchema = false)
public abstract class LocationDB extends RoomDatabase {

    private static LocationDB instance;

    public abstract LocationDao locationDao();

    public static synchronized LocationDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            LocationDB.class, "location_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
