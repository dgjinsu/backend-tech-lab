using Microsoft.AspNetCore.Mvc;
using workspace.src.Workspace.Api.Domain.Models;

namespace workspace.src.Workspace.Api.Controllers
{
    // 컨트롤러는 "WeatherForecast" 경로로 접근
    [ApiController]
    [Route("[controller]")]
    public class WeatherForecastController : ControllerBase
    {
        private static readonly string[] Summaries = new[]
        {
        "Freezing", "Bracing", "Chilly", "Cool", "Mild", "Warm", "Balmy", "Hot", "Sweltering", "Scorching"
        };

        private readonly ILogger<WeatherForecastController> _logger;

        public WeatherForecastController(ILogger<WeatherForecastController> logger)
        {
            _logger = logger;
        }

        // "GetWeatherForecase" 이름으로 엔드포인트가 생성
        [HttpGet(Name = "GetWeatherForecast")]
        public IEnumerable<WeatherForecast> Get()
        {
            return Enumerable.Range(1, 5).Select(index => new WeatherForecast
            {
                Date = DateOnly.FromDateTime(DateTime.Now.AddDays(index)),
                TemperatureC = Random.Shared.Next(-20, 55),
                Summary = Summaries[Random.Shared.Next(Summaries.Length)]
            })
            .ToArray(); //최종적으로 IEnumerable 타입의 배열로 변환하여 반환
        }
    }
}
