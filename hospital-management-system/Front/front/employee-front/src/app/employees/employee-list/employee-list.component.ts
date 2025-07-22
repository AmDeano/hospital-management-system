// src/app/employees/employee-list/employee-list.component.ts

import { Component, OnInit } from '@angular/core';
import { EmployeeService } from '../employee.service';
import { Employee } from '../employee.model';

@Component({
  selector: 'app-employee-list',
  standalone: true,
  templateUrl: './employee-list.component.html',
})
export class EmployeeListComponent implements OnInit {
  employees: Employee[] = [];

  constructor(private service: EmployeeService) {}

  ngOnInit() {
    this.service.getAllEmployees().subscribe((data) => (this.employees = data));
  }

  deleteEmployee(matricule: string) {
    this.service.deleteEmployee(matricule).subscribe(() => {
      this.employees = this.employees.filter(e => e.matricule !== matricule);
    });
  }
}
