using Demo.Domain.RepositoryInterface;
using Demo.Infrastructure.DatabaseContext;
using Demo.Infrastructure.RepositoryImplementation;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Demo.Infrastructure.DependencyInjection
{
    public static class ServiceContainer
    {
        public static IServiceCollection AddInfrastructureService(this IServiceCollection services, IConfiguration config)
        {
            services.AddDbContext<AppDbContext>(option =>
                option.UseSqlServer(
                    config.GetConnectionString("Default"),
                    b => b.MigrationsAssembly(typeof(ServiceContainer).Assembly.FullName)
                    ),
                    ServiceLifetime.Scoped
            );
            services.AddScoped<IProductRepository, ProductRepository>();
            return services;
        }
    }
}
