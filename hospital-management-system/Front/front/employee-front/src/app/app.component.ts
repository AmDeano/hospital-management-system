// src/app/app.component.ts
/*
import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'employee-front';
}
*/

import { Component, OnInit } from '@angular/core';
import { EmployeeService } from './employees/employee.service';

// Employee interface
export interface Employee {
  matricule: string;
  nom: string;
  prenom: string;
  cin?: string;
  email?: string;
  poste: string;
  departement?: string;
  employeeType: 'MEDICAL' | 'ADMINISTRATION' | 'SUPPORT';
  dateEmbauche: string;
  workDays?: string[];
  isActive?: boolean;
}

// Search criteria interface
export interface SearchCriteria {
  nom: string;
  prenom: string;
  departement: string;
  employeeType: string;
  isActive: string;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  
  // Employee management properties
  employees: Employee[] = [];
  filteredEmployees: Employee[] = [];
  searchResults: Employee[] = [];
  
  // Form properties
  newEmployee: Employee = {
    matricule: '',
    nom: '',
    prenom: '',
    cin: '',
    email: '',
    poste: '',
    departement: '',
    employeeType: 'ADMINISTRATION',
    dateEmbauche: '',
    workDays: [],
    isActive: true
  };
  
  editingEmployee: Employee | null = null;
  
  // Search properties
  searchMatricule: string = '';
  searchEmail: string = '';
  searchCin: string = '';
  searchCriteria: SearchCriteria = {
    nom: '',
    prenom: '',
    departement: '',
    employeeType: '',
    isActive: ''
  };
  
  // Display properties
  sortBy: string = 'nom';
  showInactive: boolean = false;
  
  // Static data
  workDays: string[] = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];
  departments: string[] = ['Emergency', 'Surgery', 'Pediatrics', 'Cardiology', 'Administration', 'IT', 'HR'];
  
  constructor(private employeeService: EmployeeService) {}
  
  ngOnInit() {
    this.loadEmployees();
  }
  
  // Employee CRUD operations
  loadEmployees() {
    this.employeeService.getAllEmployees().subscribe({
      next: (data) => {
        this.employees = data;
        this.filterEmployees();
      },
      error: (error) => {
        console.error('Error loading employees:', error);
        this.employees = [];
        this.filterEmployees();
      }
    });
  }
  
  saveEmployee() {
    if (this.editingEmployee) {
      // Update existing employee
      this.employeeService.updateEmployee(this.editingEmployee.matricule, this.newEmployee)
        .subscribe({
          next: (updated) => {
            const index = this.employees.findIndex(emp => emp.matricule === updated.matricule);
            if (index !== -1) {
              this.employees[index] = updated;
            }
            this.resetForm();
            this.filterEmployees();
          },
          error: (error) => console.error('Error updating employee:', error)
        });
    } else {
      // Add new employee
      this.employeeService.createEmployee(this.newEmployee)
        .subscribe({
          next: (created) => {
            this.employees.push(created);
            this.resetForm();
            this.filterEmployees();
          },
          error: (error) => console.error('Error creating employee:', error)
        });
    }
  }
  
  editEmployee(employee: Employee) {
    this.editingEmployee = { ...employee };
    this.newEmployee = { ...employee };
  }
  
  cancelEdit() {
    this.resetForm();
  }
  
  resetForm() {
    this.editingEmployee = null;
    this.newEmployee = {
      matricule: '',
      nom: '',
      prenom: '',
      cin: '',
      email: '',
      poste: '',
      departement: '',
      employeeType: 'ADMINISTRATION',
      dateEmbauche: '',
      workDays: [],
      isActive: true
    };
  }
  
  clearForm() {
    this.resetForm();
  }
  
  toggleEmployeeStatus(employee: Employee) {
    const updatedEmployee = { ...employee, isActive: !employee.isActive };
    this.employeeService.updateEmployee(employee.matricule, updatedEmployee)
      .subscribe({
        next: (updated) => {
          const index = this.employees.findIndex(emp => emp.matricule === updated.matricule);
          if (index !== -1) {
            this.employees[index] = updated;
          }
          this.filterEmployees();
        },
        error: (error) => console.error('Error updating employee status:', error)
      });
  }
  
  // Search operations
  searchByMatricule() {
    if (this.searchMatricule.trim()) {
      this.employeeService.getEmployeeById(this.searchMatricule)
        .subscribe({
          next: (employee) => this.searchResults = [employee],
          error: (error) => {
            console.error('Employee not found:', error);
            this.searchResults = [];
          }
        });
    }
  }
  
  searchByEmail() {
    if (this.searchEmail.trim()) {
      this.employeeService.searchByEmail(this.searchEmail)
        .subscribe({
          next: (employees) => this.searchResults = employees,
          error: (error) => console.error('Search error:', error)
        });
    }
  }
  
  searchByCin() {
    if (this.searchCin.trim()) {
      this.employeeService.searchByCin(this.searchCin)
        .subscribe({
          next: (employees) => this.searchResults = employees,
          error: (error) => console.error('Search error:', error)
        });
    }
  }
  
  performAdvancedSearch() {
    this.employeeService.advancedSearch(this.searchCriteria)
      .subscribe({
        next: (employees) => this.searchResults = employees,
        error: (error) => console.error('Advanced search error:', error)
      });
  }
  
  // Filter and sort operations
  loadAllEmployees() {
    this.loadEmployees();
  }
  
  loadActiveEmployees() {
    this.filteredEmployees = this.employees.filter(emp => emp.isActive);
  }
  
  loadInactiveEmployees() {
    this.filteredEmployees = this.employees.filter(emp => !emp.isActive);
  }
  
  loadAdministrationEmployees() {
    this.filteredEmployees = this.employees.filter(emp => emp.employeeType === 'ADMINISTRATION');
  }
  
  loadMedicalStaff() {
    this.filteredEmployees = this.employees.filter(emp => emp.employeeType === 'MEDICAL');
  }
  
  sortEmployees() {
    this.filteredEmployees.sort((a, b) => {
      const aValue = a[this.sortBy as keyof Employee] as string;
      const bValue = b[this.sortBy as keyof Employee] as string;
      return aValue.localeCompare(bValue);
    });
  }
  
  toggleInactiveDisplay() {
    this.filterEmployees();
  }
  
  filterEmployees() {
    this.filteredEmployees = this.showInactive 
      ? this.employees 
      : this.employees.filter(emp => emp.isActive);
    this.sortEmployees();
  }
  
  // Work days management
  isWorkDaySelected(day: string): boolean {
    return this.newEmployee.workDays?.includes(day) || false;
  }
  
  toggleWorkDay(day: string) {
    if (!this.newEmployee.workDays) {
      this.newEmployee.workDays = [];
    }
    
    const index = this.newEmployee.workDays.indexOf(day);
    if (index > -1) {
      this.newEmployee.workDays.splice(index, 1);
    } else {
      this.newEmployee.workDays.push(day);
    }
  }
  
  getWorkDayLabel(day: string): string {
    const labels: { [key: string]: string } = {
      'MONDAY': 'Monday',
      'TUESDAY': 'Tuesday',
      'WEDNESDAY': 'Wednesday',
      'THURSDAY': 'Thursday',
      'FRIDAY': 'Friday',
      'SATURDAY': 'Saturday',
      'SUNDAY': 'Sunday'
    };
    return labels[day] || day;
  }
  
  // Helper methods
  getEmployeeTypeLabel(type: string): string {
    const labels: { [key: string]: string } = {
      'MEDICAL': 'Medical Staff',
      'ADMINISTRATION': 'Administration',
      'SUPPORT': 'Support Staff'
    };
    return labels[type] || type;
  }
  
  // Statistics methods
  getActiveEmployeeCount(): number {
    return this.employees.filter(emp => emp.isActive).length;
  }
  
  getMedicalStaffCount(): number {
    return this.employees.filter(emp => emp.employeeType === 'MEDICAL' && emp.isActive).length;
  }
  
  getAdministrationCount(): number {
    return this.employees.filter(emp => emp.employeeType === 'ADMINISTRATION' && emp.isActive).length;
  }
}