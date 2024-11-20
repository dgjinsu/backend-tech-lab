package com.example.todoappserver.todo.adapter.out.mapper;

import com.example.todoappserver.todo.adapter.out.persistence.TodoEntity;
import com.example.todoappserver.todo.domain.Todo;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TodoEntityMapper {

    TodoEntityMapper INSTANCE = Mappers.getMapper(TodoEntityMapper.class);

    Todo toDomain(TodoEntity entity);

    List<Todo> toDomainList(List<TodoEntity> entityList);

    TodoEntity toEntity(Todo todo);
}
