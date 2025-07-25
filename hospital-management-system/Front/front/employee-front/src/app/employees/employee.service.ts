// src/app/employees/employee.service.ts
/*
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Employee } from './employee.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class EmployeeService {
  private baseUrl = 'http://localhost:8082/employee-service/api/employees';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Employee[]> {
    return this.http.get<Employee[]>(this.baseUrl);
  }

  getById(matricule: string): Observable<Employee> {
    return this.http.get<Employee>(`${this.baseUrl}/${matricule}`);
  }

  add(employee: Employee): Observable<Employee> {
    return this.http.post<Employee>(this.baseUrl, employee);
  }

  update(matricule: string, employee: Employee): Observable<Employee> {
    return this.http.put<Employee>(`${this.baseUrl}/${matricule}`, employee);
  }

  delete(matricule: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${matricule}`);
  }
}
*/

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Employee, SearchCriteria } from './employee.model';


@Injectable({
	providedIn :'root'
})
export class EmployeeService {
  
  private readonly API_URL = 'http://localhost:8082/employee-service/api/employees';

  constructor(private http: HttpClient) {}

  // Get all employees
  getAllEmployees(): Observable<Employee[]> {
    return this.http.get<Employee[]>(this.API_URL);
  }

  // Get employee by ID
  getEmployeeById(matricule: string): Observable<Employee> {
    return this.http.get<Employee>(`${this.API_URL}/${matricule}`);
  }

  // Create new employee
  createEmployee(employee: Employee): Observable<Employee> {
    return this.http.post<Employee>(this.API_URL, employee);
  }
  
  //add(employee: Employee): Observable<Employee> {
      //return this.http.post<Employee>(this.API_URL, employee);
    //}

  // Update employee
  updateEmployee(matricule: string, employee: Employee): Observable<Employee> {
    return this.http.put<Employee>(`${this.API_URL}/${matricule}`, employee);
  }

  // Delete employee (soft delete - mark as inactive)
  deleteEmployee(matricule: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${matricule}`);
  }

  // Search by email
  searchByEmail(email: string): Observable<Employee[]> {
    const params = new HttpParams().set('email', email);
    return this.http.get<Employee[]>(`${this.API_URL}/search/email`, { params });
  }

  // Search by CIN
  searchByCin(cin: string): Observable<Employee[]> {
    const params = new HttpParams().set('cin', cin);
    return this.http.get<Employee[]>(`${this.API_URL}/search/cin`, { params });
  }

  // Advanced search
  advancedSearch(filters: {
      nom?: string;
      prenom?: string;
      departement?: string;
      employeeType?: string;
      isActive?: boolean;
    }): Observable<Employee[]> {
      let params = new HttpParams();

      // Only append if value is provided
      Object.keys(filters).forEach(key => {
        const value = filters[key as keyof typeof filters];
        if (value !== null && value !== undefined && value !== '') {
          params = params.set(key, value.toString());
        }
      });

      return this.http.get<Employee[]>(`${this.API_URL}/search`, { params });
    }

  // Get employees by type
  getEmployeesByType(employeeType: string): Observable<Employee[]> {
    const params = new HttpParams().set('employeeType', employeeType);
    return this.http.get<Employee[]>(`${this.API_URL}/type`, { params });
  }

  // Get active employees
  getActiveEmployees(): Observable<Employee[]> {
    const params = new HttpParams().set('isActive', 'true');
    return this.http.get<Employee[]>(`${this.API_URL}/active`, { params });
  }

  // Get inactive employees
  getInactiveEmployees(): Observable<Employee[]> {
    const params = new HttpParams().set('isActive', 'false');
    return this.http.get<Employee[]>(`${this.API_URL}/inactive`, { params });
  }

  // Get employees by department
  getEmployeesByDepartment(department: string): Observable<Employee[]> {
    const params = new HttpParams().set('department', department);
    return this.http.get<Employee[]>(`${this.API_URL}/department`, { params });
  }

  // Get employee statistics
  getEmployeeStatistics(): Observable<any> {
    return this.http.get<any>(`${this.API_URL}/statistics`);
  }
}