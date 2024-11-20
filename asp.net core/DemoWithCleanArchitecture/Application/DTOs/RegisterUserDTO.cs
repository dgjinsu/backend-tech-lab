using System.ComponentModel.DataAnnotations;

namespace Application.DTOs
{
    public class RegisterUserDTO
    {
        [Required]
        public string? Name { get; set; } = string.Empty; // 빈 문자열로 초기화, null 방지

        [Required, EmailAddress]
        public string? Email { get; set; } = string.Empty;

        public string? Password {  get; set; } = string.Empty;

        [Required, Compare(nameof(Password))]
        public string? Confirmpassword { get; set; } = string.Empty;
    }
}
