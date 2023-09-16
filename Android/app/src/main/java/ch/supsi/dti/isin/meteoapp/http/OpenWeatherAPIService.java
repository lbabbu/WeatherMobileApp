package ch.supsi.dti.isin.meteoapp.http;

import org.json.JSONObject;

import ch.supsi.dti.isin.meteoapp.model.Weather;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface OpenWeatherAPIService {

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @GET("weather")
    Call<WeatherResponse> getWeatherByCity(@Query("q") String city, @Query("appid") String api_key);
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @GET("weather")
    Call<WeatherResponse> getWeatherByCoordinates(@Query("lat") double lat, @Query("lon") double lon, @Query("appid") String api_key);

}
