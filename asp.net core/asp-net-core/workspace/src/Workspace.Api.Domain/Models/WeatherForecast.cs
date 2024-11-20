namespace workspace.src.Workspace.Api.Domain.Models
{
    public class WeatherForecast
    {
        public DateOnly Date { get; set; }

        public int TemperatureC { get; set; }

        public int TemperatureF => 32 + (int)(TemperatureC / 0.5556);

        // nullable 가능
        public string? Summary { get; set; }
    }
}
