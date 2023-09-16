package ch.supsi.dti.isin.meteoapp.model;

public enum WeatherType {
    CLEAR_SKY("Clear sky","sunny"),
    FEW_CLOUDS("Few clouds","sunny_cloudy"),
    SCATTERED_CLOUDS("Scattered clouds","sunny_cloudy"),
    BROKEN_CLOUDS("Broken clouds","cloudy"),
    SHOWER_RAIN("Shower rain","rainy"),
    RAIN("Rain","rainy"),
    THUNDERSTORM("Thunderstorm","stormy"),
    SNOW("Snow","snowy"),
    MIST("Mist","foggy");

    private String mDescription;
    private String mIcon;

    WeatherType(String description, String icon) {
        mDescription = description;
        mIcon = icon;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getIcon() {
        return mIcon;
    }

}
