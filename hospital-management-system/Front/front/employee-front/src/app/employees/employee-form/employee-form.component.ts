// src/app/employees/employee-form/employee-form.component.ts

import { Component } from '@angular/core';
import { EmployeeService } from '../employee.service';
import { Employee } from '../employee.model';

@Component({
  selector: 'app-employee-form',
  templateUrl: './employee-form.component.html',
})
export class EmployeeFormComponent {
  employee: Employee = {
    matricule: '',
    nom: '',
    prenom: '',
    poste: '',
    employeeType: 'ADMINISTRATION',
    departement: '',
    dateEmbauche: new Date().toISOString().split('T')[0]
  };

  constructor(private service: EmployeeService) {}

  submit() {
    this.service.add(this.employee).subscribe(() => {
      alert('Employee added!');
    });
  }
}
