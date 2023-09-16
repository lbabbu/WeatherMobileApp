namespace MeteoApp.Models
{
    using Newtonsoft.Json;
    using System.Collections.Generic;

    public class WeatherData
    {
        [JsonProperty("coord")]
        public Coord _Coord { get; set; }

        [JsonProperty("weather")]
        public List<Weather> _Weather { get; set; }

        [JsonProperty("base")]
        public string Base { get; set; }

        [JsonProperty("main")]
        public Main _Main { get; set; }

        [JsonProperty("visibility")]
        public int Visibility { get; set; }

        [JsonProperty("wind")]
        public Wind _Wind { get; set; }

        [JsonProperty("clouds")]
        public Clouds _Clouds { get; set; }

        [JsonProperty("dt")]
        public long Dt { get; set; }

        [JsonProperty("sys")]
        public Sys _Sys { get; set; }

        [JsonProperty("timezone")]
        public int Timezone { get; set; }

        [JsonProperty("id")]
        public int Id { get; set; }

        [JsonProperty("name")]
        public string Name { get; set; }

        [JsonProperty("cod")]
        public int Cod { get; set; }

        public bool Ready { get; set; } = false;
        public string WeatherIcon { get => string.Format("https://openweathermap.org/img/wn/{0}@2x.png", _Weather[0].Icon); }

        public class Coord
        {
            [JsonProperty("lon")]
            public double Lon { get; set; }

            [JsonProperty("lat")]
            public double Lat { get; set; }
        }

        public class Weather
        {
            [JsonProperty("id")]
            public int Id { get; set; }

            [JsonProperty("main")]
            public string Main { get; set; }

            [JsonProperty("description")]
            public string Description { get; set; }

            [JsonProperty("icon")]
            public string Icon { get; set; }
        }

        public class Main
        {
            [JsonProperty("temp")]
            public double Temp { get; set; }

            [JsonProperty("feels_like")]
            public double FeelsLike { get; set; }

            [JsonProperty("temp_min")]
            public double TempMin { get; set; }

            [JsonProperty("temp_max")]
            public double TempMax { get; set; }

            [JsonProperty("pressure")]
            public int Pressure { get; set; }

            [JsonProperty("humidity")]
            public int Humidity { get; set; }

            [JsonProperty("sea_level")]
            public int SeaLevel { get; set; }

            [JsonProperty("grnd_level")]
            public int GrndLevel { get; set; }

            public double TempCelsius { get=>ConvertTemperature(Temp);}
            public double TempMinCelsius { get => ConvertTemperature(TempMin); }
            public double TempMaxCelsius { get => ConvertTemperature(TempMax); }
            private double ConvertTemperature(double k)
            {
                return Math.Round(k - 273.15,2);
            }
        }

        public class Wind
        {
            [JsonProperty("speed")]
            public double Speed { get; set; }

            [JsonProperty("deg")]
            public int Deg { get; set; }
        }

        public class Clouds
        {
            [JsonProperty("all")]
            public int All { get; set; }
        }

        public class Sys
        {
            [JsonProperty("type")]
            public int Type { get; set; }

            [JsonProperty("id")]
            public int Id { get; set; }

            [JsonProperty("message")]
            public double Message { get; set; }

            [JsonProperty("country")]
            public string Country { get; set; }

            [JsonProperty("sunrise")]
            public long Sunrise { get; set; }

            [JsonProperty("sunset")]
            public long Sunset { get; set; }
        }
    }
}


