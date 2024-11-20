using Application.DTOs;

namespace Application.Contract
{
    public interface IUser
    {
        Task<RegistrationResponse> RegisterUserAsync(RegisterUserDTO registerUserDTO);
        Task<LoginResponse> LoginUserAsync(LoginDTO loginDTO);
        Task<IEnumerable<UserResponse>> GetUserList();
    }
}
