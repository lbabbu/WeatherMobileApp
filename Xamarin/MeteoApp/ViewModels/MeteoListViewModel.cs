using MeteoApp.Models;
using MeteoApp.Repository;
using System.Collections.ObjectModel;


namespace MeteoApp.ViewModels
{
    public class MeteoListViewModel : BaseViewModel
    {
        ObservableCollection<City> _cities;

        public ObservableCollection<City> Cities
        {
            get { return _cities; }
            set
            {
                _cities = value;
                OnPropertyChanged();
            }
        }

        public MeteoListViewModel()
        {
            Cities = new ObservableCollection<City>();
            string[] cities = new string[] { "Rome", "Paris", "London" };
            foreach (var item in cities)
            {
                var e = new City();
                e.Name = item;

                Cities.Add(e);
            }
        }
    }
}
