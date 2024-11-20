using Demo.Application.MappingImplementation;
using Demo.Application.MappingInterface;
using Demo.Application.UseCaseImplementation;
using Demo.Application.UseCaseInterface;
using Microsoft.Extensions.DependencyInjection;

namespace Demo.Application.DependencyInjection
{
    public static class ServiceContainer
    {
        public static IServiceCollection AddApplicationService(this IServiceCollection services)
        {
            services.AddScoped<IProductService, ProductService>();
            services.AddScoped<IProductMapper, ProductMapper>();

            return services;
        }
    }
}
