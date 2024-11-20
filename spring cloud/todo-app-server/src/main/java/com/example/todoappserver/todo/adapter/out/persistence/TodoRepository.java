package com.example.todoappserver.todo.adapter.out.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TodoRepository extends JpaRepository<TodoEntity, Long> {
    @Modifying
    @Query("delete from TodoEntity t where t.isCompleted = true and t.memberId = :memberId")
    int deleteCompletedTodo(@Param("memberId") Long memberId);

    @Query("select t from TodoEntity t where t.memberId = :memberId")
    List<TodoEntity> findAllByMemberId(@Param("memberId") Long memberId);
}
