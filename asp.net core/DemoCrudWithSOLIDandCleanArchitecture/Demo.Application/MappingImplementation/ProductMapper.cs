using Demo.Application.MappingInterface;
using Demo.Application.ProductDTOs;
using Demo.Domain.ProductEntity;

namespace Demo.Application.MappingImplementation
{
    public class ProductMapper : IProductMapper
    {
        public ProductDto MapToDto(Product product)
        {
            return new ProductDto
            {
                Id = product.Id,
                Name = product.Name,
                Price = product.Price,
                Stock = product.Stock
            };
        }

        public Product MapToEntity(CreateProductDto createDto)
        {
            return new Product
            {
                Name = createDto.Name,
                Price = createDto.Price,
                Stock = createDto.Stock
            };
        }

        public Product MapToEntity(UpdateProductDto updateDto)
        {
            return new Product
            {
                Name = updateDto.Name,
                Price = updateDto.Price,
                Stock = updateDto.Stock
            };
        }
    }
}
