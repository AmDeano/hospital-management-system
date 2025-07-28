// src/app/employees/employee-list/employee-list.component.ts

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Employee, EmployeeType, SearchCriteria } from '../employee.model';
import { EmployeeService } from '../employee.service';

@Component({
  selector: 'app-employee-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './employee-list.component.html',
  styleUrl: './employee-list.component.css'
})
export class EmployeeListComponent implements OnInit {
  employees: Employee[] = [];
  loading = false;
  error: string | null = null;
  
  // Search functionality
  searchCriteria: SearchCriteria = {};
  showAdvancedSearch = false;
  
  // Enum references for template
  EmployeeType = EmployeeType;

  constructor(private employeeService: EmployeeService) {}

  ngOnInit(): void {
    this.loadEmployees();
  }

  loadEmployees(): void {
    this.loading = true;
    this.error = null;
    
    this.employeeService.getAllEmployees().subscribe({
      next: (employees) => {
        this.employees = employees;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Error loading employees: ' + error.message;
        this.loading = false;
      }
    });
  }

  searchEmployees(): void {
      if (Object.keys(this.searchCriteria).length === 0) {
        this.loadEmployees();
        return;
      }

      this.loading = true;
      this.employeeService.advancedSearch(this.searchCriteria).subscribe({
        next: (employees: Employee[]) => {
          this.employees = employees;
          this.loading = false;
        },
        error: (error: any) => {
          this.error = 'Erreur lors de la recherche : ' + error.message;
          this.loading = false;
        }
      });
    }
  

  clearSearch(): void {
    this.searchCriteria = {};
    this.loadEmployees();
  }

  deleteEmployee(matricule: string): void {
    if (confirm('Are you sure you want to delete this employee?')) {
      this.employeeService.deleteEmployee(matricule).subscribe({
        next: () => {
          this.loadEmployees(); // Refresh the list
        },
        error: (error) => {
          this.error = 'Error deleting employee: ' + error.message;
        }
      });
    }
  }

  toggleEmployeeStatus(employee: Employee): void {
    const updatedEmployee = { ...employee, isActive: !employee.isActive };
    
    this.employeeService.updateEmployee(employee.matricule, updatedEmployee).subscribe({
      next: () => {
        this.loadEmployees(); // Refresh the list
      },
      error: (error) => {
        this.error = 'Error updating employee status: ' + error.message;
      }
    });
  }

  getEmployeeTypeDisplay(type: EmployeeType): string {
    return type.replace('_', ' ');
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleDateString();
  }
}