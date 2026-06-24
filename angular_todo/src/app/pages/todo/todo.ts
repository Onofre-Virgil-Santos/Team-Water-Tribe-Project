import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-todo',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './todo.html',
  styleUrl: './todo.css',
})
export class Todo {
  newTodoTitle = '';
  todos: string[] = [];

  addTodo() {
    if (this.newTodoTitle.trim() === '') return;

    this.todos.push(this.newTodoTitle);
    this.newTodoTitle = '';
  }
}