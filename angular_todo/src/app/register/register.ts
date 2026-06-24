import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Register {
  // Inject HttpClient to make HTTP requests to the backend
  private http = inject(HttpClient);

  // A FormGroup is a collection of FormControls that represents the entire form.
  // It tracks the value and validation state of each child control as a single unit.
  // In the template, we bind it to a <form> element with [formGroup]="registerForm",
  // which is what enables (ngSubmit) to fire when the form is submitted.
  // Each FormControl inside holds the value for one input field (initialized to '').
  registerForm = new FormGroup({
    username: new FormControl(''),
    password: new FormControl(''),
  });

  // Signals to hold feedback messages displayed in the template
  successMessage = signal('');
  errorMessage = signal('');

  // Called when the form is submitted via (ngSubmit)
  onSubmit(): void {
    // Clear any previous messages before a new attempt
    this.successMessage.set('');
    this.errorMessage.set('');

    // Build the request body from the form values
    const body = {
      username: this.registerForm.value.username,
      password: this.registerForm.value.password,
    };

    // POST to the backend registration endpoint and handle the response
    // Arguments:
    //   1st arg: the URL of the backend endpoint
    //   2nd arg: the request body, sent as JSON by default
    //   3rd arg: options object —
    //     observe: 'response' gives us the full HttpResponse (including status code),
    //              rather than just the parsed body
    //     responseType: 'text' tells Angular to treat the response body as plain text
    //                   instead of attempting to parse it as JSON
    this.http.post('http://localhost:8080/register', body, { observe: 'response', responseType: 'text' }).subscribe({
      // .subscribe() triggers the HTTP request and lets us handle the result.
      // http.post() returns an Observable — it won't execute until subscribed to.
      // The object passed to subscribe() is an Observer with callback functions:
      //   next: called when the request completes successfully (2xx status)
      //   error: called when the request fails (network error or non-2xx status)
      // Angular's HttpClient automatically routes non-2xx responses to the error callback.

      next: (response) => {
        // A 201 status indicates the account was created successfully
        if (response.status === 201) {
          this.successMessage.set('Registration successful!');
        }
      },
      error: (err) => {
        // Display the error message returned by the backend, or a generic fallback
        const message = typeof err.error === 'string' ? err.error : (err.error?.message ?? 'An unexpected error occurred');
        this.errorMessage.set(message);
      },
    });
  }
}