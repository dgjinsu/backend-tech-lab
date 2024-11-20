using Demo.Domain.ProductEntity;
using Demo.Domain.RepositoryInterface;
using Demo.Infrastructure.DatabaseContext;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Demo.Infrastructure.RepositoryImplementation
{
    public class ProductRepository(AppDbContext context) : IProductRepository
    {
        public async Task<long> AddAsync(Product product)
        {
            await context.Products.AddAsync(product);
            await context.SaveChangesAsync();
            return product.Id;
        }

        public async Task DeleteAsync(long id)
        {
            var product = await context.Products.FindAsync(id);
            if (product != null)
            {
                context.Products.Remove(product);
                await context.SaveChangesAsync();
            }
        }

        // 변경 추적 감지 X 
        public async Task<IEnumerable<Product>> GetAllAsync() =>
            await context.Products.AsNoTracking().ToListAsync();

        public async Task<Product?> GetByIdAsync(long id) =>
            await context.Products.FindAsync(id);


        public async Task UpdateAsync(Product product)
        {
            context.Products.Update(product);
            await context.SaveChangesAsync();
        }
    }
}