// src/main.ts
/*
import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { routes } from './app/app-routing.module';
import {AppComponent} from './app/app.component';

bootstrapApplication(AppComponent, {
  providers: [provideRouter(routes)]
});
*/

import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { AppComponent } from './app/app.component';
import { routes } from './app/app.routes';
import { EmployeeService } from './app/employees/employee.service';
import { provideHttpClient } from '@angular/common/http';

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
	provideHttpClient(),
    EmployeeService
  ]
});