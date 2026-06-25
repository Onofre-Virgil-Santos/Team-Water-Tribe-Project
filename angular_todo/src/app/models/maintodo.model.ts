export interface MainTodo {
  id: number;
  task: string;
  description: string;
  completed: boolean;
  expanded?: boolean;
}