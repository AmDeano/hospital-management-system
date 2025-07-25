// src/app/app.component.ts
/*
import { Component, OnInit } from '@angular/core';
import { EmployeeService } from './employees/employee.service';
import { Employee } from './employees/employee.model';

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
  employees: Employee[] = [];
  filteredEmployees: Employee[] = [];
  searchResults: Employee[] = [];

  newEmployee: Employee = this.getEmptyEmployee();
  editingEmployee: Employee | null = null;

  searchMatricule = '';
  searchEmail = '';
  searchCin = '';
  searchCriteria: SearchCriteria = {
    nom: '',
    prenom: '',
    departement: '',
    employeeType: '',
    isActive: ''
  };

  sortBy: keyof Employee = 'nom';
  showInactive = false;

  readonly workDays = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];
  readonly departments = ['Emergency', 'Surgery', 'Pediatrics', 'Cardiology', 'Administration', 'IT', 'HR'];

  constructor(private employeeService: EmployeeService) {}

  ngOnInit() {
    this.loadEmployees();
  }

  // --- CRUD ---

  loadEmployees() {
    this.employeeService.getAllEmployees().subscribe({
      next: data => {
        this.employees = data;
        this.applyFilters();
      },
      error: err => {
        console.error('Error loading employees:', err);
        this.employees = [];
        this.applyFilters();
      }
    });
  }

  saveEmployee() {
    const req = this.editingEmployee
      ? this.employeeService.updateEmployee(this.editingEmployee.matricule, this.newEmployee)
      : this.employeeService.createEmployee(this.newEmployee);

    req.subscribe({
      next: saved => {
        if (this.editingEmployee) {
          const idx = this.employees.findIndex(e => e.matricule === saved.matricule);
          if (idx !== -1) this.employees[idx] = saved;
        } else {
          this.employees.push(saved);
        }
        this.resetForm();
        this.applyFilters();
      },
      error: err => console.error('Error saving employee:', err)
    });
  }

  editEmployee(emp: Employee) {
    this.editingEmployee = { ...emp };
    this.newEmployee = { ...emp };
  }

  cancelEdit() {
    this.resetForm();
  }

  toggleEmployeeStatus(emp: Employee) {
    const updated = { ...emp, isActive: !emp.isActive };
    this.employeeService.updateEmployee(emp.matricule, updated).subscribe({
      next: saved => {
        const idx = this.employees.findIndex(e => e.matricule === saved.matricule);
        if (idx !== -1) this.employees[idx] = saved;
        this.applyFilters();
      },
      error: err => console.error('Error toggling status:', err)
    });
  }

  // --- Search ---

  searchByMatricule() {
    if (!this.searchMatricule.trim()) return;
    this.employeeService.getEmployeeById(this.searchMatricule).subscribe({
      next: emp => this.searchResults = [emp],
      error: () => this.searchResults = []
    });
  }

  searchByEmail() {
    if (!this.searchEmail.trim()) return;
    this.employeeService.searchByEmail(this.searchEmail).subscribe({
      next: res => this.searchResults = res,
      error: err => console.error(err)
    });
  }

  searchByCin() {
    if (!this.searchCin.trim()) return;
    this.employeeService.searchByCin(this.searchCin).subscribe({
      next: res => this.searchResults = res,
      error: err => console.error(err)
    });
  }

  performAdvancedSearch() {
    this.employeeService.advancedSearch(this.searchCriteria).subscribe({
      next: res => this.searchResults = res,
      error: err => console.error(err)
    });
  }

  // --- Filters/Sort ---

  applyFilters() {
    this.filteredEmployees = this.showInactive
      ? [...this.employees]
      : this.employees.filter(e => e.isActive);
    this.sortEmployees();
  }

  sortEmployees() {
    this.filteredEmployees.sort((a, b) =>
      (a[this.sortBy] || '').toString().localeCompare((b[this.sortBy] || '').toString())
    );
  }

  toggleInactiveDisplay() {
    this.showInactive = !this.showInactive;
    this.applyFilters();
  }

  // --- Helpers ---

  resetForm() {
    this.editingEmployee = null;
    this.newEmployee = this.getEmptyEmployee();
  }

  getEmptyEmployee(): Employee {
    return {
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

  toggleWorkDay(day: string) {
    const days = this.newEmployee.workDays || [];
    const idx = days.indexOf(day);
    if (idx > -1) {
      days.splice(idx, 1);
    } else {
      days.push(day);
    }
    this.newEmployee.workDays = days;
  }

  isWorkDaySelected(day: string): boolean {
    return this.newEmployee.workDays?.includes(day) ?? false;
  }

  getEmployeeTypeLabel(type: string): string {
    return {
      'MEDICAL': 'Medical Staff',
      'ADMINISTRATION': 'Administration',
      'SUPPORT': 'Support Staff'
    }[type] || type;
  }

  getWorkDayLabel(day: string): string {
    return day.charAt(0) + day.slice(1).toLowerCase();
  }

  // --- Stats ---

  getActiveEmployeeCount() {
    return this.employees.filter(e => e.isActive).length;
  }

  getMedicalStaffCount() {
    return this.employees.filter(e => e.employeeType === 'MEDICAL_STAFF' && e.isActive).length;
  }

  getAdministrationCount() {
    return this.employees.filter(e => e.employeeType === 'ADMINISTRATION' && e.isActive).length;
  }
}
*/
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
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'hospital-management-system';
}
