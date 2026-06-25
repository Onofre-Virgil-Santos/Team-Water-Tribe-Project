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

  createTodo(task: string): Observable<MainTodo> {
    return this.http.post<MainTodo>(this.apiUrl, { task }, this.getHeaders());
  }

  deleteTodo(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, this.getHeaders());
  }
}