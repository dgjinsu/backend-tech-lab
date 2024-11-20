using Demo.Application.MappingInterface;
using Demo.Application.ProductDTOs;
using Demo.Application.UseCaseInterface;
using Demo.Domain.RepositoryInterface;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Demo.Application.UseCaseImplementation
{
    public class ProductService(IProductRepository productRepository, IProductMapper productMapper) : IProductService
    {
        public async Task<long> AddProductAsync(CreateProductDto productDto)
        {
            var product = productMapper.MapToEntity(productDto);
            long productId = await productRepository.AddAsync(product);
            return productId;
        }

        public async Task DeleteProductAsync(long id) => await productRepository.DeleteAsync(id);

        public async Task<IEnumerable<ProductDto>> GetAllProductsAsync()
        {
            var products = await productRepository.GetAllAsync();
            return products.Select(product => productMapper.MapToDto(product)).ToList();
        }

        public async Task<ProductDto?> GetProductByIdAsync(long id)
        {
            var product = await productRepository.GetByIdAsync(id);

            return product == null ? null : productMapper.MapToDto(product);
        }

        public async Task UpdateProductAsync(UpdateProductDto productDto)
        {
            var product = productMapper.MapToEntity(productDto);
            await productRepository.UpdateAsync(product); // PK 값을 기준으로 데이터베이스에서 해당 엔티티를 찾고 업데이트
        }
    }
}
