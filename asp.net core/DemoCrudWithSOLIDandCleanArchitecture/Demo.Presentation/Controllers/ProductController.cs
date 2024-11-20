using Demo.Application.ProductDTOs;
using Demo.Application.UseCaseInterface;
using Microsoft.AspNetCore.Mvc;

namespace Demo.Presentation.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ProductController(IProductService productService) : ControllerBase
    {
        [HttpGet]
        public async Task<IActionResult> GetAll()
        {
            var products = await productService.GetAllProductsAsync();
            return Ok(products);
        }

        [HttpGet("{Id}")]
        public async Task<IActionResult> GetProduct(long Id)
        {
            var productDto = await productService.GetProductByIdAsync(Id);
            if (productDto == null) return NotFound();
            return Ok(productDto);
        }

        [HttpPost]
        public async Task<ActionResult> SaveProduct(CreateProductDto dto)
        {
            var productId = await productService.AddProductAsync(dto);
            return CreatedAtAction(nameof(GetProduct), new { id = productId }, dto);
        }

        [HttpPut("{Id}")]
        public async Task<IActionResult> UpdateProduct(long Id, [FromBody] UpdateProductDto dto)
        {
            if (Id != dto.Id)
            {
                return BadRequest();
            }
            await productService.UpdateProductAsync(dto);
            return NoContent();
        }

        [HttpDelete("{Id}")]
        public async Task<IActionResult> Delete(long Id)
        {
            await productService.DeleteProductAsync(Id);
            return NoContent();
        }
    }
}
