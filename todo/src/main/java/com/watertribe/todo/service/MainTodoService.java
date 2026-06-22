package com.watertribe.todo.service;

import com.watertribe.todo.dto.MainTodoRequest;
import com.watertribe.todo.dto.MainTodoResponse;
import com.watertribe.todo.entity.MainTodo;
import com.watertribe.todo.entity.User;
import com.watertribe.todo.repository.MainTodoRepository;
import com.watertribe.todo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainTodoService {

    private final MainTodoRepository mainTodoRepository;
    private final UserRepository userRepository;

    public MainTodoResponse createMainTodo(
            MainTodoRequest request,
            Authentication authentication
    ) {
        User user = getLoggedInUser(authentication);

        MainTodo mainTodo = MainTodo.builder()
                .task(request.getTask())
                .description(request.getDescription())
                .completed(false)
                .user(user)
                .build();

        MainTodo savedTodo = mainTodoRepository.save(mainTodo);

        return mapToResponse(savedTodo);
    }

    public List<MainTodoResponse> getAllMainTodos(Authentication authentication) {
        User user = getLoggedInUser(authentication);

        return mainTodoRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public MainTodoResponse getMainTodoById(
            Long id,
            Authentication authentication
    ) {
        User user = getLoggedInUser(authentication);

        MainTodo mainTodo = mainTodoRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Main todo not found"));

        return mapToResponse(mainTodo);
    }

    public MainTodoResponse updateMainTodo(
            Long id,
            MainTodoRequest request,
            Authentication authentication
    ) {
        User user = getLoggedInUser(authentication);

        MainTodo mainTodo = mainTodoRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Main todo not found"));

        mainTodo.setTask(request.getTask());
        mainTodo.setDescription(request.getDescription());
        mainTodo.setCompleted(request.isCompleted());

        MainTodo updatedTodo = mainTodoRepository.save(mainTodo);

        return mapToResponse(updatedTodo);
    }

    public void deleteMainTodo(
            Long id,
            Authentication authentication
    ) {
        User user = getLoggedInUser(authentication);

        MainTodo mainTodo = mainTodoRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Main todo not found"));

        mainTodoRepository.delete(mainTodo);
    }

    private User getLoggedInUser(Authentication authentication) {
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private MainTodoResponse mapToResponse(MainTodo mainTodo) {
        return MainTodoResponse.builder()
                .id(mainTodo.getId())
                .task(mainTodo.getTask())
                .description(mainTodo.getDescription())
                .completed(mainTodo.isCompleted())
                .createdAt(mainTodo.getCreatedAt())
                .updatedAt(mainTodo.getUpdatedAt())
                .build();
    }
}