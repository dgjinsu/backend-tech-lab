using Microsoft.EntityFrameworkCore;
using workspace.src.Workspace.Api.Domain.Models;


namespace workspace.src.Workspace.Api
{
    // 애플리케이션의 진입점(Entry Point) 역할
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            // 서비스 컨테이너에 컨트롤러 서비스 등록 
            // API 요청 처리 가능
            builder.Services.AddControllers();

            // DI 컨테이너에 데이터베이스 컨텍스트를 추가
            builder.Services.AddDbContext<TodoContext>(opt =>
                opt.UseInMemoryDatabase("TodoList"));
            builder.Services.AddEndpointsApiExplorer();
            builder.Services.AddSwaggerGen();

            var app = builder.Build();

            if (app.Environment.IsDevelopment())
            {
                app.UseSwagger();
                app.UseSwaggerUI();
            }

            // Http 요청을 Https로 리다이렉션하여 보안 강화
            app.UseHttpsRedirection();

            // 모든 컨트롤러의 엔드포인트가 애플리케이션에 매핑 
            app.MapControllers();

            app.Run();
        }
    }
}
