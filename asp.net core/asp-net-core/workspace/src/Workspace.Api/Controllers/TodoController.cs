using Microsoft.AspNetCore.Mvc;
using workspace.src.Workspace.Api.Domain.Dtos;
using workspace.src.Workspace.Api.Domain.Models;

namespace workspace.src.Workspace.Api.Controllers
{
    [ApiController]
    [Route("/api/[controller]")]
    public class TodoController : ControllerBase
    {
        private readonly TodoContext _context;

        public TodoController(TodoContext context)
        {
            _context = context;
        }

        // 요청 경로: /api/Post
        [HttpPost]
        public async Task<ActionResult<TodoItem>> PostTodoItem(TodoItemDto dto)
        {
            var todoItem = new TodoItem { IsComplete = dto.IsComplete, Name = dto.Name };

            // // 새 TodoItem을 데이터베이스에 추가
            _context.TodoItems.Add(todoItem);
            await _context.SaveChangesAsync();

            // nameof(GetTodoItem)은 경로 매핑을 위한 메서드
            // CreatedAtAction을 사용하여 생성된 리소스의 URI와 데이터를 반환
            // nameof를 사용해 문자열을 감싸 컴파일 검사 해줌
            return CreatedAtAction(nameof(GetTodoItem), new { id = todoItem.Id }, ItemToDto(todoItem)); // URI 생성에 사용할 메서드 이름, 매개변수, 응답 본문
            // 201 응답에선 새로 생성된 리소스의 위치를 함께 제공하는 것이 RESTful API 규칙
        }

        // 요청 경로: /api/Post/{id}
        [HttpGet("{id}")]
        public async Task<ActionResult<TodoItemDto>> GetTodoItem(long id)
        {
            var todoItem = await _context.TodoItems.FindAsync(id);

            if (todoItem == null)
            {
                return NotFound();
            }

            return ItemToDto(todoItem);
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> putTodoItem(long id, TodoItemDto dto)
        {
            if (id != dto.Id)
            {
                return BadRequest();
            }

            var todoItem = await _context.TodoItems.FindAsync(id);
            if (todoItem == null)
            {
                return NotFound();
            }

            todoItem.Name = dto.Name;
            todoItem.IsComplete = dto.IsComplete;

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (Exception ex)
            {
                throw;
            }

            return NoContent();
        }

        private bool TodoItemExists(long id)
        {
            return _context.TodoItems.Any(e => e.Id == id);
        }


        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteTodoItem(long id)
        {
            var todoItem = await _context.TodoItems.FindAsync(id);
            if (todoItem == null)
            {
                return NotFound();
            }

            _context.TodoItems.Remove(todoItem);
            await _context.SaveChangesAsync();

            return NoContent();
        }

        private static TodoItemDto ItemToDto(TodoItem todoItem) =>
            new TodoItemDto
            {
                Id = todoItem.Id,
                Name = todoItem.Name,
                IsComplete = todoItem.IsComplete
            };
    }
}
