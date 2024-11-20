using Microsoft.EntityFrameworkCore;

namespace workspace.src.Workspace.Api.Domain.Models
{
    // 데이터베이스 컨텍스트 추가
    // 엔터티와 데이터베이스 테이블 간의 매핑을 설정하는 역할을 합니다.
    public class TodoContext : DbContext
    {
        // 데이터베이스 연결 정보를 주입받을 수 있습니다.
        public TodoContext(DbContextOptions<TodoContext> options) : base(options)
        {
        }

        // TodoItems 테이블을 나타내는 DbSet
        // TodoItem 엔터티를 통해 데이터베이스 테이블과 상호작용할 수 있다.
        public DbSet<TodoItem> TodoItems { get; set; } = null!;
    }
}
