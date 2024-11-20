package com.example.tempserver.domain.temp.repository;

import com.example.tempserver.domain.temp.entity.Temp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TempRepository extends JpaRepository<Temp, Long> {
    Temp findByTodoId(Long todoId);

    @Query("select t from Temp t where t.todoId in :todoIds")
    List<Temp> findByTodoIds(List<Long> todoIds);
}
