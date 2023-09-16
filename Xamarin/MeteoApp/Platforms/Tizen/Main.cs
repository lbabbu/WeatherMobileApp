using System;
using SQLite;
using Microsoft.Maui;
using Microsoft.Maui.Hosting;

namespace MeteoApp;

class Program : MauiApplication
{
	private static TestDatabase testDatabase;

    public static TestDatabase Database
    {
        get
        {
            if (database == null) // se l'istanza è nulla, la creo
                database = new TestDatabase();
            return database; // ritorno l'istanza
        }
    }

    protected override MauiApp CreateMauiApp() => MauiProgram.CreateMauiApp();

	static void Main(string[] args)
	{
		var app = new Program();
		app.Run(args);
	}
}

