package ch.supsi.dti.isin.meteoapp.model;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import ch.supsi.dti.isin.meteoapp.activities.MainActivity;
import ch.supsi.dti.isin.meteoapp.http.OpenWeatherAPIService;
import ch.supsi.dti.isin.meteoapp.http.WeatherResponse;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.time.LocalTime;

public class TemperatureWorker extends Worker {

    private Context mContext;
    private static final String TAG = "TemperatureWorker";
    private LocalDateTime lastNotificationTime;

    public TemperatureWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        mContext = context;
        lastNotificationTime = LocalDateTime.now().minus(Duration.of(1, ChronoUnit.MINUTES));
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.v(TAG, "Inside Temperature Worker");
        try {
            // Recupera le coordinate dalla classe LocationsHolder
            LocationsHolder locationsHolder = LocationsHolder.get(mContext);
            GPSCoordinates currentLocation = locationsHolder.getCurrentLocation(mContext);


            if (currentLocation == null) {
                return Result.failure();
            }
            System.out.println("Current location from TEMPERATURE WORKER: " + currentLocation);
            Log.i(TAG, "Worker started at " + new Date().toString());


            // Esegui il controllo della temperatura e invia la notifica se necessario
            checkTemperature(currentLocation);
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Error in TemperatureWorker: ", e);
            return Result.failure();
        }

    }

    private void checkTemperature(GPSCoordinates coordinates) throws IOException {
        // Utilizzare le coordinate ottenute dalla posizione corrente per ottenere la temperatura
        double latitude = coordinates.getLatitude();
        double longitude = coordinates.getLongitude();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OpenWeatherAPIService service = retrofit.create(OpenWeatherAPIService.class);
        Call<WeatherResponse> weatherRequest = service.getWeatherByCoordinates(latitude, longitude, "8ee77de7c0d74ad71c1aa7e069710ff7");


        Response<WeatherResponse> response = weatherRequest.execute();
        if (response.isSuccessful()) {
            WeatherResponse weatherResponse = response.body();
            assert weatherResponse != null;
            double currentTemperature = weatherResponse.getMain().getTemp() - 273.15;
            System.out.println("Current temperature: " + currentTemperature);
            System.out.println("Recent temperature: " + MainActivity.recentTemperature);

            if (MainActivity.recentTemperature == Double.MIN_VALUE) {
                MainActivity.recentTemperature = currentTemperature;
                System.out.println("Recent temperature in if == 0: " + MainActivity.recentTemperature);
            } else {
                double temperatureDifference = Math.abs(currentTemperature - MainActivity.recentTemperature);
                System.out.println("Temperature difference: " + temperatureDifference);
                LocalDateTime now = LocalDateTime.now();

                // Invia una notifica ogni volta che la temperatura è diversa dalla temperatura recente
                if (temperatureDifference >= 0.1 || currentTemperature == MainActivity.recentTemperature) {
                    lastNotificationTime = now;
                    sendNotification(currentTemperature, MainActivity.recentTemperature);
                    MainActivity.recentTemperature = currentTemperature;
                }
            }
        }
    }

    private void sendNotification(double currentTemperature, double previousTemperature) {
        String text = "";
        if (currentTemperature > previousTemperature) {
            text = "T° aumentata di " + String.format("%.1f", Math.abs(currentTemperature - previousTemperature)) + " °C" +
                    " (ORA: " + String.format("%.1f", currentTemperature) + "°C)";
        } else if (currentTemperature < previousTemperature) {
            text = "T° diminuita di " + String.format("%.1f", Math.abs(currentTemperature - previousTemperature)) + " °C" +
                    " (ORA: " + String.format("%.1f", currentTemperature) + "°C)";
        } else {
            text = "La temperatura è rimasta stabile";
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "temperatureChannel")
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentTitle("Cambiamento di temperatura")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        notificationManager.notify(0, builder.build());
    }
}

