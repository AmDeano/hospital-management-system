// src/app/app.component.ts

import { Component, OnInit } from '@angular/core';
import { RouterOutlet, RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { TokenService } from './auth/token.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    CommonModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatDividerModule
  ],
  template: `
    <mat-toolbar color="primary" class="app-navbar" *ngIf="isLoggedIn">
      <span class="spacer"></span>
      <span class="app-title">Hospital Management System</span>
      <span class="spacer"></span>

      <button mat-icon-button [matMenuTriggerFor]="navMenu" title="Navigation">
        <mat-icon>menu</mat-icon>
      </button>
      <mat-menu #navMenu="matMenu">
        <button mat-menu-item [routerLink]="['/dashboard']">
          <mat-icon>dashboard</mat-icon>
          <span>Dashboard</span>
        </button>
        <button mat-menu-item [routerLink]="['/employees']">
          <mat-icon>people</mat-icon>
          <span>Employees</span>
        </button>
        <button mat-menu-item [routerLink]="['/doctors']">
          <mat-icon>medical_services</mat-icon>
          <span>Doctors</span>
        </button>
        <button mat-menu-item [routerLink]="['/nurses']">
          <mat-icon>local_hospital</mat-icon>
          <span>Nurses</span>
        </button>
        <button mat-menu-item [routerLink]="['/patients']">
          <mat-icon>person</mat-icon>
          <span>Patients</span>
        </button>
        <mat-divider></mat-divider>
        <button mat-menu-item (click)="logout()">
          <mat-icon>logout</mat-icon>
          <span>Logout</span>
        </button>
      </mat-menu>

      <button mat-icon-button (click)="logout()" title="Logout">
        <mat-icon>logout</mat-icon>
      </button>
    </mat-toolbar>

    <main class="app-content">
      <router-outlet></router-outlet>
    </main>
  `,
  styles: [`
    .app-navbar {
      position: sticky;
      top: 0;
      z-index: 100;
    }

    .app-title {
      font-weight: 500;
      font-size: 1.2em;
    }

    .spacer {
      flex: 1 1 auto;
    }

    .app-content {
      display: flex;
      flex-direction: column;
      min-height: calc(100vh - 64px);
    }
  `]
})
export class AppComponent implements OnInit {
  title = 'hospital-management-system';
  isLoggedIn = false;

  constructor(
    private tokenService: TokenService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.isLoggedIn = this.tokenService.isLoggedIn();
  }

  logout(): void {
    this.tokenService.logout();
    this.isLoggedIn = false;
    this.router.navigate(['/login']);
  }
}
