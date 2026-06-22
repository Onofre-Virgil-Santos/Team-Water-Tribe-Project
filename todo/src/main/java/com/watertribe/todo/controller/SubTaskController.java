package com.watertribe.todo.controller;

import com.watertribe.todo.dto.SubTaskRequest;
import com.watertribe.todo.dto.SubTaskResponse;
import com.watertribe.todo.service.SubTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/main-todos/{mainTodoId}/subtasks")
@RequiredArgsConstructor
public class SubTaskController {

    private final SubTaskService subTaskService;

    @PostMapping
    public SubTaskResponse createSubTask(
            @PathVariable Long mainTodoId,
            @RequestBody SubTaskRequest request,
            Authentication authentication
    ) {
        return subTaskService.createSubTask(mainTodoId, request, authentication);
    }

    @GetMapping
    public List<SubTaskResponse> getAllSubTasks(
            @PathVariable Long mainTodoId,
            Authentication authentication
    ) {
        return subTaskService.getAllSubTasks(mainTodoId, authentication);
    }

    @GetMapping("/{id}")
    public SubTaskResponse getSubTaskById(
            @PathVariable Long mainTodoId,
            @PathVariable Long id,
            Authentication authentication
    ) {
        return subTaskService.getSubTaskById(mainTodoId, id, authentication);
    }

    @PutMapping("/{id}")
    public SubTaskResponse updateSubTask(
            @PathVariable Long mainTodoId,
            @PathVariable Long id,
            @RequestBody SubTaskRequest request,
            Authentication authentication
    ) {
        return subTaskService.updateSubTask(mainTodoId, id, request, authentication);
    }

    @DeleteMapping("/{id}")
    public String deleteSubTask(
            @PathVariable Long mainTodoId,
            @PathVariable Long id,
            Authentication authentication
    ) {
        subTaskService.deleteSubTask(mainTodoId, id, authentication);
        return "Sub task deleted successfully";
    }
}
