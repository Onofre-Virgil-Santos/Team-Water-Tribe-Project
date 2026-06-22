package com.watertribe.todo.repository;

import com.watertribe.todo.entity.SubTask;
import com.watertribe.todo.entity.MainTodo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubTaskRepository extends JpaRepository<SubTask, Long> {

    List<SubTask> findByMainTodo(MainTodo mainTodo);

    Optional<SubTask> findByIdAndMainTodo(Long id, MainTodo mainTodo);
}