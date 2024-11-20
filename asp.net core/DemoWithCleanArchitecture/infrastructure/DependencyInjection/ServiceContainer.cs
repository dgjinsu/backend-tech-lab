using Application.Contract;
using infrastructure.Data;
using infrastructure.Repo;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.IdentityModel.Tokens;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace infrastructure.DependencyInjection
{
    public static class ServiceContainer
    {
        // IServiceCollection에 의존성 주입 구성
        public static IServiceCollection InfrastructureServices(this IServiceCollection services, IConfiguration configuration)
        {
            // AddDbContext 메서드를 사용하여 AppDbContext를 DI 컨테이너에 등록
            services.AddDbContext<AppDbContext>(options =>
                // SQL Server 데이터베이스를 사용하도록 설정
                options.UseSqlServer(
                    configuration.GetConnectionString("Default"),
                    b => b.MigrationsAssembly(typeof(ServiceContainer).Assembly.FullName)
                ),
                ServiceLifetime.Scoped // // DbContext의 생명 주기를 Scoped로 설정 (요청마다 인스턴스를 새로 생성)
            );

            // JWT Authentication 추가
            services.AddAuthentication(options =>
            {
                options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
                options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
            }).AddJwtBearer(options =>
            {
                options.TokenValidationParameters = new TokenValidationParameters
                {
                    ValidateIssuer = true, // 토큰의 발급자(issuer)를 검증
                    ValidateAudience = true, // 토큰의 수신자(audience)를 검증
                    ValidateLifetime = true, // 토큰의 유효 기간을 검증
                    ValidateIssuerSigningKey = true, // 토큰의 서명 키를 검증

                    ValidIssuer = configuration["Jwt:Issuer"], // 발급자의 유효성을 검증할 때 사용할 값
                    ValidAudience = configuration["Jwt:Audience"], // 수신자의 유효성을 검증할 때 사용할 값
                    IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(configuration["Jwt:Key"]!)) // 토큰을 검증할 때 사용할 서명 키
                };

                // Bearer Prefix 없이 요청왔을 때 추가 
                options.Events = new JwtBearerEvents
                {
                    OnMessageReceived = context =>
                    {
                        var token = context.Request.Headers["Authorization"].FirstOrDefault();
                        if (!string.IsNullOrEmpty(token) && !token.StartsWith("Bearer "))
                        {
                            context.Request.Headers["Authorization"] = $"Bearer {token}";
                        }
                        return Task.CompletedTask;
                    }
                };
            });

            services.AddScoped<IUser, UserRepo>(); // IUser 인터페이스와 UserRepo 클래스 간의 의존성을 DI 컨테이너에 등록
            // 의존성 반환
            return services;
        }
    }
}
