using System.ComponentModel.DataAnnotations;

namespace workspace.src.Workspace.Api.Domain.Dtos
{
    public class TodoItemDto
    {
        public long Id { get; set; }
        [Required(ErrorMessage = "Name is required.")]
        [StringLength(100, ErrorMessage = "Name cannot be longer than 100 characters.")]
        public string? Name { get; set; }
        public bool IsComplete { get; set; }
    }
}
