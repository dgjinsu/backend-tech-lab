using Demo.Application.ProductDTOs;
using Demo.Domain.ProductEntity;

namespace Demo.Application.MappingInterface
{
    public interface IProductMapper
    {
        ProductDto MapToDto(Product product);
        Product MapToEntity(CreateProductDto createDto);
        Product MapToEntity(UpdateProductDto updateDto);
    }
}
