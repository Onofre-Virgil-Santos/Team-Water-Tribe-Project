package com.watertribe.todo.service;

import com.watertribe.todo.dto.SubTaskRequest;
import com.watertribe.todo.dto.SubTaskResponse;
import com.watertribe.todo.entity.MainTodo;
import com.watertribe.todo.entity.SubTask;
import com.watertribe.todo.entity.User;
import com.watertribe.todo.repository.MainTodoRepository;
import com.watertribe.todo.repository.SubTaskRepository;
import com.watertribe.todo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubTaskService {

    private final SubTaskRepository subTaskRepository;
    private final MainTodoRepository mainTodoRepository;
    private final UserRepository userRepository;

    public SubTaskResponse createSubTask(
            Long mainTodoId,
            SubTaskRequest request,
            Long userId
    ) {
        MainTodo mainTodo = getMainTodoForUser(mainTodoId, userId);

        SubTask subTask = SubTask.builder()
                .task(request.getTask())
                .description(request.getDescription())
                .completed(false)
                .mainTodo(mainTodo)
                .build();

        return mapToResponse(subTaskRepository.save(subTask));
    }

    public List<SubTaskResponse> getAllSubTasks(
            Long mainTodoId,
            Long userId
    ) {
        MainTodo mainTodo = getMainTodoForUser(mainTodoId, userId);

        return subTaskRepository.findByMainTodo(mainTodo)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public SubTaskResponse getSubTaskById(
            Long mainTodoId,
            Long id,
            Long userId
    ) {
        MainTodo mainTodo = getMainTodoForUser(mainTodoId, userId);

        SubTask subTask = subTaskRepository.findByIdAndMainTodo(id, mainTodo)
                .orElseThrow(() -> new RuntimeException("Sub task not found"));

        return mapToResponse(subTask);
    }

    public SubTaskResponse updateSubTask(
            Long mainTodoId,
            Long id,
            SubTaskRequest request,
            Long userId
    ) {
        MainTodo mainTodo = getMainTodoForUser(mainTodoId, userId);

        SubTask subTask = subTaskRepository.findByIdAndMainTodo(id, mainTodo)
                .orElseThrow(() -> new RuntimeException("Sub task not found"));

        subTask.setTask(request.getTask());
        subTask.setDescription(request.getDescription());
        subTask.setCompleted(request.isCompleted());

        return mapToResponse(subTaskRepository.save(subTask));
    }

    public void deleteSubTask(
            Long mainTodoId,
            Long id,
            Long userId
    ) {
        MainTodo mainTodo = getMainTodoForUser(mainTodoId, userId);

        SubTask subTask = subTaskRepository.findByIdAndMainTodo(id, mainTodo)
                .orElseThrow(() -> new RuntimeException("Sub task not found"));

        subTaskRepository.delete(subTask);
    }

    private MainTodo getMainTodoForUser(Long mainTodoId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mainTodoRepository.findByIdAndUser(mainTodoId, user)
                .orElseThrow(() -> new RuntimeException("Main todo not found"));
    }

    private SubTaskResponse mapToResponse(SubTask subTask) {
        return SubTaskResponse.builder()
                .id(subTask.getId())
                .task(subTask.getTask())
                .description(subTask.getDescription())
                .completed(subTask.isCompleted())
                .mainTodoId(subTask.getMainTodo().getId())
                .createdAt(subTask.getCreatedAt())
                .updatedAt(subTask.getUpdatedAt())
                .build();
    }
}
