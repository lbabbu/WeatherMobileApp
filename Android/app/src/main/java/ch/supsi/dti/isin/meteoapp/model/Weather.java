package ch.supsi.dti.isin.meteoapp.model;

public class Weather {
    //openweathermap.org API json object
    private double mTemperature;
    private double mPressure;
    private double mHumidity;
    private double mTempMin;
    private double mTempMax;
    private double mWindSpeed;
    private double mWindDeg;
    private double mClouds;
    private WeatherType mWeatherType;
    private double mRain;//3h rain volume
    private double mSnow;//3h snow volume
    private String mWeatherDescription;

    public Weather() {
    }

    public double getmTemperature() {
        return mTemperature;
    }

    public void setmTemperature(double mTemperature) {
        this.mTemperature = mTemperature;
    }

    public double getmPressure() {
        return mPressure;
    }

    public void setmPressure(double mPressure) {
        this.mPressure = mPressure;
    }

    public double getmHumidity() {
        return mHumidity;
    }

    public void setmHumidity(double mHumidity) {
        this.mHumidity = mHumidity;
    }

    public double getmTempMin() {
        return mTempMin;
    }

    public void setmTempMin(double mTempMin) {
        this.mTempMin = mTempMin;
    }

    public double getmTempMax() {
        return mTempMax;
    }

    public void setmTempMax(double mTempMax) {
        this.mTempMax = mTempMax;
    }

    public double getmWindSpeed() {
        return mWindSpeed;
    }

    public void setmWindSpeed(double mWindSpeed) {
        this.mWindSpeed = mWindSpeed;
    }

    public double getmWindDeg() {
        return mWindDeg;
    }

    public void setmWindDeg(double mWindDeg) {
        this.mWindDeg = mWindDeg;
    }

    public double getmClouds() {
        return mClouds;
    }

    public void setmClouds(double mClouds) {
        this.mClouds = mClouds;
    }

    public double getmRain() {
        return mRain;
    }

    public void setmRain(double mRain) {
        this.mRain = mRain;
    }

    public double getmSnow() {
        return mSnow;
    }

    public void setmSnow(double mSnow) {
        this.mSnow = mSnow;
    }

    public String getmWeatherDescription() {
        return mWeatherDescription;
    }

    public void setmWeatherDescription(String mWeatherDescription) {
        this.mWeatherDescription = mWeatherDescription;
    }

    public WeatherType getmWeatherType() {
        return mWeatherType;
    }

    public void setmWeatherType(WeatherType mWeatherType) {
        this.mWeatherType = mWeatherType;
    }
}
