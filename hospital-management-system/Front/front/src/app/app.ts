import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { EmployeeService, Employee } from './services/employee.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, HttpClientModule, FormsModule],
  providers: [EmployeeService],
  templateUrl: './app.html',
  styleUrls: ['./app.scss']
})
export class App implements OnInit {
  employees: Employee[] = [];
  searchedEmployee?: Employee;
  searchMatricule = '';
  newEmployee: Employee = {
    matricule: '',
    nom: '',
    poste: '',
    dateEmbauche: ''
  };

  constructor(private employeeService: EmployeeService) {}

  ngOnInit() {
    this.loadEmployees();
  }

  loadEmployees() {
    this.employeeService.getAllEmployees().subscribe(data => {
      this.employees = data;
    });
  }

  addEmployee() {
    this.employeeService.createEmployee(this.newEmployee).subscribe(() => {
      this.newEmployee = { matricule: '', nom: '', poste: '', dateEmbauche: '' };
      this.loadEmployees();
    });
  }

  searchEmployee() {
    if (!this.searchMatricule) return;
    this.employeeService.getEmployeeByMatricule(this.searchMatricule).subscribe(data => {
      this.searchedEmployee = data;
    });
  }

  deleteEmployee(matricule: string) {
    this.employeeService.deleteEmployee(matricule).subscribe(() => {
      this.loadEmployees();
    });
  }
}

