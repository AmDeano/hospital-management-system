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
import { DashboardComponent } from './dashboard/dashboard.component';
import { PatientListComponent } from './patients/patient-list/patient-list.component';
import { DoctorListComponent } from './doctors/doctor-list/doctor-list.component';
import { DoctorDashboardComponent } from './doctors/doctor-dashboard/doctor-dashboard.component';
import { NurseListComponent } from './nurses/nurse-list/nurse-list.component';

const authGuard = () => {
  const tokens = inject(TokenService);
  return tokens.isLoggedIn() ? true : (location.href = '/login', false);
};

export const routes: Routes = [
  // Auth routes
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: '', component: AuthShellComponent, children: [
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
  ]},

  // Protected routes
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },

  // Employee routes
  { path: 'employees', component: EmployeeListComponent, canActivate: [authGuard] },
  { path: 'employees/add', component: EmployeeFormComponent, canActivate: [authGuard] },

  // Doctor routes
  { path: 'doctors', component: DoctorListComponent, canActivate: [authGuard] },
  { path: 'doctors/add', component: DoctorListComponent, canActivate: [authGuard] },
  { path: 'dashboard/doctors/:doctorMatricule', component: DoctorDashboardComponent, canActivate: [authGuard] },

  // Nurse routes
  { path: 'nurses', component: NurseListComponent, canActivate: [authGuard] },
  { path: 'nurses/add', component: NurseListComponent, canActivate: [authGuard] },

  // Patient routes
  { path: 'patients', component: PatientListComponent, canActivate: [authGuard] },
  { path: 'patients/add', component: PatientListComponent, canActivate: [authGuard] },
  { path: 'patients/:id', component: PatientListComponent, canActivate: [authGuard] },
  { path: 'patients/:id/edit', component: PatientListComponent, canActivate: [authGuard] },

  // Wildcard
  { path: '**', redirectTo: 'dashboard' },
];