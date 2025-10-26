// src/app/app-routing.module.ts

import { Routes } from '@angular/router';
import { EmployeeListComponent } from './employees/employee-list/employee-list.component';
import { EmployeeFormComponent } from './employees/employee-form/employee-form.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { AuthGuard } from './auth/auth-guard';
import { inject } from '@angular/core';
import { TokenService } from './auth/token.service';
import { AuthShellComponent } from './auth/auth.shell';


const authGuard = () => {
  const tokens = inject(TokenService);
  return tokens.isLoggedIn() ? true : (location.href = '/login', false);
};


export const routes: Routes = [
   { path: '', pathMatch: 'full', redirectTo: 'login' },
   { path: '', component: AuthShellComponent, children: [
       { path: 'login', component: LoginComponent },
       { path: 'register', component: RegisterComponent },
     ]},

     { path: 'employees', component: EmployeeListComponent, canActivate: [authGuard] },
     { path: '**', redirectTo: 'login' },
  { path: 'employees', component: EmployeeListComponent },
  { path: 'employees/add', component: EmployeeFormComponent },
];