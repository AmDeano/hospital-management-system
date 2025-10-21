// src/app/auth/auth.shell.ts
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-auth-shell',
  standalone: true,
  imports: [RouterOutlet],
  template: `
  <div class="center-wrapper">
    <div class="auth-card">
      <div class="brand">
        <img src="assets/lock.svg" width="28" height="28" alt="logo">
        <h1>Hospital Access</h1>
      </div>
      <router-outlet></router-outlet>
    </div>
  </div>
  `
})
export class AuthShellComponent {}
