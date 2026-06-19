package com.watertribe.todo.controller;

import com.watertribe.todo.entity.MainTodo;
import com.watertribe.todo.entity.SubTask;
import com.watertribe.todo.repository.MainTodoRepository;
import com.watertribe.todo.repository.SubTaskRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/todos/{mainTodoId}/subtasks")
public class SubTaskController {

    private final SubTaskRepository subTaskRepository;
    private final MainTodoRepository mainTodoRepository;

    public SubTaskController(SubTaskRepository subTaskRepository,
                             MainTodoRepository mainTodoRepository) {
        this.subTaskRepository = subTaskRepository;
        this.mainTodoRepository = mainTodoRepository;
    }

    @GetMapping
    public ResponseEntity<List<SubTask>> getAll(@PathVariable Long mainTodoId) {
        MainTodo mainTodo = mainTodoRepository.findById(mainTodoId)
        .orElseThrow(() -> new RuntimeException("MainTodo not found"));
        return ResponseEntity.ok(subTaskRepository.findByMainTodo(mainTodo));
    }

    @PostMapping
    public ResponseEntity<SubTask> create(@PathVariable Long mainTodoId,
                                          @RequestBody SubTask subTask) {
        MainTodo mainTodo = mainTodoRepository.findById(mainTodoId)
                .orElseThrow(() -> new RuntimeException("MainTodo not found"));
        subTask.setMainTodo(mainTodo);
        return ResponseEntity.ok(subTaskRepository.save(subTask));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubTask> update(@PathVariable Long mainTodoId,
                                          @PathVariable Long id,
                                          @RequestBody SubTask updated) {
        MainTodo mainTodo = mainTodoRepository.findById(mainTodoId)
                .orElseThrow(() -> new RuntimeException("MainTodo not found"));
        SubTask subTask = subTaskRepository.findByIdAndMainTodo(id, mainTodo)
                .orElseThrow(() -> new RuntimeException("SubTask not found"));
        subTask.setTask(updated.getTask());
        subTask.setDescription(updated.getDescription());
        subTask.setCompleted(updated.isCompleted());
        return ResponseEntity.ok(subTaskRepository.save(subTask));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long mainTodoId,
                                       @PathVariable Long id) {
        MainTodo mainTodo = mainTodoRepository.findById(mainTodoId)
                .orElseThrow(() -> new RuntimeException("MainTodo not found"));
        SubTask subTask = subTaskRepository.findByIdAndMainTodo(id, mainTodo)
                .orElseThrow(() -> new RuntimeException("SubTask not found"));
        subTaskRepository.delete(subTask);
        return ResponseEntity.noContent().build();
    }
}
