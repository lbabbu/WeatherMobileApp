using MeteoApp.ViewModels;

namespace MeteoApp;

[QueryProperty(nameof(SelectedItemViewModel), "vm")]
public partial class MeteoItemPage : ContentPage
{
    private SelectedItemViewModel vm;
    public SelectedItemViewModel SelectedItemViewModel { get => vm; set { vm = value; OnPropertyChanged(); } }

    public MeteoItemPage()
    {
        InitializeComponent();
    }

    protected override void OnAppearing()
    {
        base.OnAppearing();
        BindingContext = SelectedItemViewModel;
    }
}