using MeteoApp.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using MeteoApp.Models;
namespace MeteoApp.Repository
{
    internal interface IWeatherRepository
    {

        Task<WeatherData> GetWeatherByCity(string city);
        Task<WeatherData> GetWeatherFromGPSAsync();
    }
}
