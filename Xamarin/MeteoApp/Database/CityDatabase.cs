using SQLite;
using System.Threading.Tasks;
using System.Collections.Generic;
using Newtonsoft.Json;
using MeteoApp.Models;

public class CityDatabase
{
    readonly SQLiteAsyncConnection _database;

    public CityDatabase(string dbPath)
    {
        _database = new SQLiteAsyncConnection(dbPath);
        _database.CreateTableAsync<City>().Wait();
    }

    public Task<List<City>> GetCitiesAsync()
    {
        return _database.Table<City>().ToListAsync();
    }

    public Task<City> GetCityAsync(int id)
    {
        return _database.Table<City>()
                        .Where(i => i.ID == id)
                        .FirstOrDefaultAsync();
    }

    public Task<int> SaveCityAsync(City city)
    {
        if (city.ID != 0)
        {
            return _database.UpdateAsync(city);
        }
        else
        {
            return _database.InsertAsync(city);
        }
    }

    public Task<int> DeleteCityAsync(City city)
    {
        return _database.DeleteAsync(city);
    }

}

