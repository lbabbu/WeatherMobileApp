package ch.supsi.dti.isin.meteoapp.model;

import androidx.annotation.NonNull;

public class GPSCoordinates {
private double mLatitude;
    private double mLongitude;

    public GPSCoordinates(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    @NonNull
    @Override
    public String toString() {
        return "Latitude: " + mLatitude + " Longitude: " + mLongitude;
    }
}
