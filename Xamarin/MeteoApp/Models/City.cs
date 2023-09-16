using Newtonsoft.Json;
using SQLite;

namespace MeteoApp.Models
{
    public class City
    {
        public int ID { get; set; }
        public string Name { get; set; }

        [Ignore]
        public WeatherData WeatherData
        {
            get { return JsonConvert.DeserializeObject<WeatherData>(WeatherDataJson); }
            set { WeatherDataJson = JsonConvert.SerializeObject(value); }
        }

        public string WeatherDataJson { get; set; }
    }
}
