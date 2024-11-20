using Application.Contract;
using Application.DTOs;
using Domain.Entities;
using infrastructure.Data;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.IdentityModel.Tokens;
using System;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

namespace infrastructure.Repo
{
    internal class UserRepo : IUser
    {
        private readonly AppDbContext appDbContext;
        private readonly IConfiguration configuration;

        public UserRepo(AppDbContext appDbContext, IConfiguration configuration)
        {
            this.appDbContext = appDbContext;
            this.configuration = configuration;
        }
        public async Task<LoginResponse> LoginUserAsync(LoginDTO loginDTO)
        {
            var getUser = await FindUserByEmail(loginDTO.Email!); // null이 아님을 보장한다는 의미에서 ! 를 붙임

            if (getUser == null) return new LoginResponse(false, "User not found");

            bool checkPassword = BCrypt.Net.BCrypt.Verify(loginDTO.Password, getUser.Password);
            if (checkPassword)
            {
                return new LoginResponse(true, "Login successfully", GenerateJWTToken(getUser));
            }
            else
            {
                return new LoginResponse(false, "Invalid credentials");
            }
        }

        private string GenerateJWTToken(ApplicationUser getUser)
        {
            var securityKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(configuration["Jwt:Key"]!));
            Console.WriteLine(configuration["Jwt:Key"]!);
            var credentials = new SigningCredentials(securityKey, SecurityAlgorithms.HmacSha256);
            var userClaims = new[]
            {
                new Claim(ClaimTypes.NameIdentifier, getUser.Id.ToString()),
                new Claim(ClaimTypes.Name, getUser.Name!),
                new Claim(ClaimTypes.Email, getUser.Email!)
            };

            var token = new JwtSecurityToken(
                issuer: configuration["Jwt:Issuer"],
                audience: configuration["Jwt:Audience"],
                claims: userClaims,
                expires: DateTime.Now.AddDays(5),
                signingCredentials: credentials
                );

            return new JwtSecurityTokenHandler().WriteToken(token);
        }

        private async Task<ApplicationUser> FindUserByEmail(string email) =>
            await appDbContext.Users.FirstOrDefaultAsync(u => u.Email == email);

        public async Task<RegistrationResponse> RegisterUserAsync(RegisterUserDTO registerUserDTO)
        {
            var getUser = await FindUserByEmail(registerUserDTO.Email!);
            if (getUser != null)
                return new RegistrationResponse(false, "User Already exist");

            appDbContext.Users.Add(new ApplicationUser()
            {
                Name = registerUserDTO.Name,
                Email = registerUserDTO.Email,
                Password = BCrypt.Net.BCrypt.HashPassword(registerUserDTO.Password)
            });

            await appDbContext.SaveChangesAsync();
            return new RegistrationResponse(true, "Registration completed");
        }

        public async Task<IEnumerable<UserResponse>> GetUserList()
        {
            var userList = await appDbContext.Users.ToListAsync();

            var userResponseList = userList.Select(user => new UserResponse
            {
                Id = user.Id,
                Name = user.Name,
                Email = user.Email,
            }).ToList();

            return userResponseList;
        }
    }
}
