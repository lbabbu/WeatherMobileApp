package ch.supsi.dti.isin.meteoapp.model;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import ch.supsi.dti.isin.meteoapp.http.WeatherResponse;

@Entity(tableName = "location")
public class Location {
    @PrimaryKey()
    @ColumnInfo(name = "id")
    @NonNull
    private UUID Id;

    @ColumnInfo(name = "name")
    private String mName;

    @Ignore
    private WeatherResponse mWeather;

    public UUID getId() {
        return Id;
    }

    public void setId(UUID id) {
        Id = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public WeatherResponse getmWeather() {
        return mWeather;
    }

    public void setmWeather(WeatherResponse mWeather) {
        this.mWeather = mWeather;
    }

    public Location() {
        Id = UUID.randomUUID();
    }

}