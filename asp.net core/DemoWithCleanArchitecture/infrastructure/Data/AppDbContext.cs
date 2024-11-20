using Domain.Entities;
using Microsoft.EntityFrameworkCore;

namespace infrastructure.Data
{
    internal class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options)
        {
        }

        public DbSet<ApplicationUser> Users { get; set; } // 테이블 명
    }
}
