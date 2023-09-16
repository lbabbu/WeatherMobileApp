using SQLite;

namespace MeteoApp;

public partial class App : Application
{
    private static CityDatabase database;

    public static CityDatabase Database
    {
        get
        {
            if (database == null)
            {
                var dbPath = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), "cities.db");
                database = new CityDatabase(dbPath);
            }
            return database;
        }
    }

    public App()
    {
        InitializeComponent();

        MainPage = new MeteoListPage();
    }
}
