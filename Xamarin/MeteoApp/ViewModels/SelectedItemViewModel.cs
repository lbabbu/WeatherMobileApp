using MeteoApp.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MeteoApp.ViewModels
{
    public class SelectedItemViewModel : BaseViewModel
    {
        
        private City _city=new City();
        public City City
        {
            get { return _city; }
            set
            {
                _city = value;
                OnPropertyChanged();
            }
        }
        public SelectedItemViewModel()
        {
        }

    }
}
