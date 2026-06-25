import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MainTodo } from '../models/maintodo.model';

@Injectable({
  providedIn: 'root'
})
export class MainTodoService {

  private apiUrl = 'http://localhost:8080/api/main-todos';

  constructor(private http: HttpClient) {}

  getAllTodos(): Observable<MainTodo[]> {
    return this.http.get<MainTodo[]>(this.apiUrl);
  }

  createTodo(task: string): Observable<MainTodo> {
    return this.http.post<MainTodo>(this.apiUrl, { task });
  }

  deleteTodo(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}