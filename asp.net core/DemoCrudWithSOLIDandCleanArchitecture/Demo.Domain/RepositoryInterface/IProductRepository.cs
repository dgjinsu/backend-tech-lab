using Demo.Domain.ProductEntity;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Demo.Domain.RepositoryInterface
{
    public interface IProductRepository
    {
        Task<IEnumerable<Product>> GetAllAsync();
        Task<Product?> GetByIdAsync(long id);
        Task<long> AddAsync(Product product);
        Task UpdateAsync(Product product);
        Task DeleteAsync(long id);
    }
}
