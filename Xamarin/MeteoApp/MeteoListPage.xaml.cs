using MeteoApp.Models;
using MeteoApp.Repository;
using MeteoApp.ViewModels;
namespace MeteoApp;

public partial class MeteoListPage : Shell
{
    private MeteoListViewModel _viewModel;

    public Dictionary<string, Type> Routes { get; private set; } = new Dictionary<string, Type>();

    public MeteoListPage()
    {
        InitializeComponent();
        RegisterRoutes();

        _viewModel = new MeteoListViewModel();
        LoadCitiesFromDatabase();  // load cities from the database
        BindingContext = _viewModel;
    }

    private async void LoadCitiesFromDatabase()
    {
        var cities = await App.Database.GetCitiesAsync();
        foreach (var city in cities)
        {
            _viewModel.Cities.Add(city);
        }
    }

    private void RegisterRoutes()
    {
        Routes.Add("entrydetails", typeof(MeteoItemPage));

        foreach (var item in Routes)
            Routing.RegisterRoute(item.Key, item.Value);
    }

    private void OnListItemSelected(object sender, SelectedItemChangedEventArgs e)
    {
        if (e.SelectedItem != null)
        {

            LoadPage(e.SelectedItem);
        }
    }

    private async void OnItemAdded(object sender, EventArgs e)
    {
        await ShowPrompt();
    }

    private async Task ShowPrompt()
    {
        string result = await DisplayPromptAsync("Add new location", "Insert location name");
        if (string.IsNullOrEmpty(result))
            return;

        var repository = WeatherRepository.Instance;
        var weatherData = await repository.GetWeatherByCity(result);

        if (weatherData != null)
        {
            var city = new City { Name = result, WeatherData = weatherData };
            await App.Database.SaveCityAsync(city);
            _viewModel.Cities.Add(city);

            await DisplayAlert("Add City", $"City {result} was added successfully.", "OK");
        }
        else
        {
            await DisplayAlert("Add City", $"City {result} does not exist.", "OK");
        }
    }

    private void Geolocate_Clicked(object sender, EventArgs e)
    {


        LoadPage(null, true);

    }

    private async void LoadPage(object e, bool isGps = false)
    {
        City location = new City();
        IWeatherRepository repository = WeatherRepository.Instance;
        this.LoadingSpinner.IsRunning = true;
        if (!isGps)
        {
            location = e as City;

            location.WeatherData = await repository.GetWeatherByCity(location.Name);
        }
        else
        {
            WeatherData meteoAndLocation = await repository.GetWeatherFromGPSAsync();
            location.Name = meteoAndLocation.Name;
            location.WeatherData = meteoAndLocation;
        }



        SelectedItemViewModel vm = new SelectedItemViewModel();
        vm.City = location;

        var navigationParameter = new Dictionary<string, object>
            {
                {"vm", vm }
            };
        this.LoadingSpinner.IsRunning = false;
        _ = Current.GoToAsync($"entrydetails", navigationParameter);

    }
}
