import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MainTodo } from '../models/maintodo.model';

@Injectable({
  providedIn: 'root'
})
export class MainTodoService {

  private apiUrl = 'http://localhost:8080/api/main-todos';

  constructor(private http: HttpClient) {}

  private getHeaders(){
    const token = localStorage.getItem('auth_token');
    return {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`
      })
    };
  }

  getAllTodos(): Observable<MainTodo[]> {
    return this.http.get<MainTodo[]>(this.apiUrl, this.getHeaders());
  }

  createTodo(todoData: Partial<MainTodo>): Observable<MainTodo> {
    return this.http.post<MainTodo>(this.apiUrl, todoData, this.getHeaders());
  }

  updateTodo(todoId: number, todoData: Partial<MainTodo>): Observable<MainTodo> {
    return this.http.put<MainTodo>(`${this.apiUrl}/${todoId}`, todoData, this.getHeaders());
  }

  deleteTodo(todoId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${todoId}`, this.getHeaders());
  }
}