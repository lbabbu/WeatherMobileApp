using System.Net;
using MeteoApp.Models;
using Newtonsoft.Json;

namespace MeteoApp.Repository
{
    internal class WeatherRepository : IWeatherRepository

    {
        private const string _API_KEY = "8ee77de7c0d74ad71c1aa7e069710ff7";
        private const string _API_URL = "https://api.openweathermap.org/data/2.5/weather?";
        private static WeatherRepository _instance;
        private WeatherRepository()
        {
        }
        public static WeatherRepository Instance
        {
            get
            {
                if (_instance == null)
                {
                    _instance = new WeatherRepository();
                }
                return _instance;
            }
        }
        public async Task<WeatherData> GetWeatherByCity(string city)
        {
            try
            {
                var json = await _getHttpJson(string.Format("{0}q={1}&appid={2}", _API_URL, city, _API_KEY));
                return _parseJSON(json);
            }
            catch (HttpRequestException e)
            {
                if (e.StatusCode == HttpStatusCode.NotFound) // Check if the status code is 404
                {
                    return null; // Return null when the city does not exist
                }

                throw; // If the status code is not 404, rethrow the exception
            }
        
    }

        public async Task<WeatherData> GetWeatherFromGPSAsync()
        {
            Models.Location location = new Models.Location(0, 0);
            var gps = await Geolocation.Default.GetLocationAsync();
            if (gps != null)
            {
                location.Latitude = gps.Latitude;
                location.Longitude = gps.Longitude;
            }
       ;

            WeatherData weather = _parseJSON(await _getHttpJson(string.Format("{0}lat={1}&lon={2}&appid={3}", _API_URL, location.Latitude, location.Longitude, _API_KEY)));
            return weather;

        }
        private async Task<string> _getHttpJson(string url)
        {
            var httpClient = new HttpClient();
            var json = await httpClient.GetStringAsync(url);
            return json;
            
        }
        private WeatherData _parseJSON(string json)
        {
            var weather = JsonConvert.DeserializeObject<WeatherData>(json);
            return weather;
        }

    }
}
