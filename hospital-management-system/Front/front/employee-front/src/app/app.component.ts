// src/app/app.component.ts

import { Component } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  template: `
    <nav>
      <a routerLink="/employees">Employee List</a> |
      <a routerLink="/employees/add">Add Employee</a>
    </nav>
    <router-outlet></router-outlet>
  `,
  styleUrls: ['./employees/employee-list/employee-list.component.css']
})
export class AppComponent {
  title = 'hospital-management-system';
}
