import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MainTodoService } from '../../services/maintodo.service';
import { MainTodo } from '../../models/maintodo.model';

@Component({
  selector: 'app-todo',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './todo.html',
  styleUrl: './todo.css',
})
export class Todo implements OnInit {
  newTodoTitle = '';
  todos: MainTodo[] = [];

  constructor(private mainTodoService: MainTodoService) {}

  ngOnInit() {
    this.loadTodos();
  }

  loadTodos() {
    this.mainTodoService.getAllTodos().subscribe(data => {
      this.todos = data;
    });
  }

  addTodo() {
    if (!this.newTodoTitle.trim()) {
      return;
    }

    this.mainTodoService.createTodo(this.newTodoTitle).subscribe(() => {
      this.newTodoTitle = '';
      this.loadTodos();
    });
  }
}