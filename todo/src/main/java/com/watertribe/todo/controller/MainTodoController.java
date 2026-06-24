package com.watertribe.todo.controller;

import com.watertribe.todo.dto.MainTodoRequest;
import com.watertribe.todo.dto.MainTodoResponse;
import com.watertribe.todo.service.MainTodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/main-todos")
@RequiredArgsConstructor
public class MainTodoController {

    private final MainTodoService mainTodoService;

    @PostMapping
    public MainTodoResponse createMainTodo(
            @RequestBody MainTodoRequest request,
            Authentication authentication
    ) {
        return mainTodoService.createMainTodo(request, authentication);
    }

    @GetMapping
    public List<MainTodoResponse> getAllMainTodos(Authentication authentication) {
        return mainTodoService.getAllMainTodos(authentication);
    }

    @GetMapping("/{id}")
    public MainTodoResponse getMainTodoById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return mainTodoService.getMainTodoById(id, authentication);
    }

    @PutMapping("/{id}")
    public MainTodoResponse updateMainTodo(
            @PathVariable Long id,
            @RequestBody MainTodoRequest request,
            Authentication authentication
    ) {
        return mainTodoService.updateMainTodo(id, request, authentication);
    }

    @DeleteMapping("/{id}")
    public String deleteMainTodo(
            @PathVariable Long id,
            Authentication authentication
    ) {
        mainTodoService.deleteMainTodo(id, authentication);
        return "Main todo deleted successfully";
    }
}