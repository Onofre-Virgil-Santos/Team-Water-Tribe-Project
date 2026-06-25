import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../auth/auth.service';

interface MainTodo {
  id: number;
  task: string;
  description: string;
  completed: boolean;
  createdAt: string;
  updatedAt: string;
}

@Component({
  selector: 'app-todo',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './todo.html',
  styleUrl: './todo.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Todo implements OnInit {
  private http = inject(HttpClient);

  private readonly apiUrl = 'http://localhost:8080/api/main-todos';

  todos = signal<MainTodo[]>([]);
  errorMessage = signal('');
  editingTodoId = signal<number | null>(null);

  todoForm = new FormGroup({
    task: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.maxLength(255)] }),
    description: new FormControl('', { nonNullable: true }),
    completed: new FormControl(false, { nonNullable: true }),
  });

  ngOnInit(): void {
    this.loadTodos();
  }

  loadTodos(): void {
    this.http.get<MainTodo[]>(this.apiUrl).subscribe({
      next: (todos) => this.todos.set(todos),
      error: () => this.errorMessage.set('Failed to load todos.'),
    });
  }

  onSubmit(): void {
    this.errorMessage.set('');
    if (this.todoForm.invalid) return;

    const todoData = {
      task: this.todoForm.value.task,
      description: this.todoForm.value.description,
      completed: this.todoForm.value.completed,
    };

    const editId = this.editingTodoId();

    if (editId !== null) {
      this.http.put<MainTodo>(`${this.apiUrl}/${editId}`, todoData).subscribe({
        next: () => { this.resetForm(); this.loadTodos(); },
        error: (err) => this.errorMessage.set(typeof err.error === 'string' ? err.error : 'Failed to update todo.'),
      });
    } else {
      this.http.post<MainTodo>(this.apiUrl, todoData).subscribe({
        next: () => { this.resetForm(); this.loadTodos(); },
        error: (err) => this.errorMessage.set(typeof err.error === 'string' ? err.error : 'Failed to create todo.'),
      });
    }
  }

  toggleComplete(todo: MainTodo): void {
    this.http.put<MainTodo>(`${this.apiUrl}/${todo.id}`, {
      task: todo.task,
      description: todo.description,
      completed: !todo.completed,
    }).subscribe({
      next: () => this.loadTodos(),
      error: () => this.errorMessage.set('Failed to update todo.'),
    });
  }

  editTodo(todo: MainTodo): void {
    this.editingTodoId.set(todo.id);
    this.todoForm.setValue({
      task: todo.task,
      description: todo.description ?? '',
      completed: todo.completed,
    });
  }

  deleteTodo(id: number): void {
    this.http.delete(`${this.apiUrl}/${id}`).subscribe({
      next: () => this.loadTodos(),
      error: () => this.errorMessage.set('Failed to delete todo.'),
    });
  }

  cancelEdit(): void {
    this.resetForm();
  }

  private resetForm(): void {
    this.editingTodoId.set(null);
    this.todoForm.reset();
  }
}
