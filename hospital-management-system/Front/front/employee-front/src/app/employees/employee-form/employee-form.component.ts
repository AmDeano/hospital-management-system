// src/app/employees/employee-form/employee-form.component.ts
/*
import { Component } from '@angular/core';
import { EmployeeService } from '../employee.service';
import { Employee } from '../employee.model';

@Component({
  selector: 'app-employee-form',
  standalone: true,
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
*/

// src/app/employees/employee-form/employee-form.component.ts

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { Employee, EmployeeType, WorkDay, EmployeeCreateRequest } from '../employee.model';
import { EmployeeService } from '../employee.service';

@Component({
  selector: 'app-employee-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './employee-form.component.html',
  styleUrl: './employee-form.component.css'
})
export class EmployeeFormComponent implements OnInit {
  employee: EmployeeCreateRequest = {
    matricule: '',
    nom: '',
    prenom: '',
    poste: '',
    employeeType: EmployeeType.ADMINISTRATION,
    dateEmbauche: new Date().toISOString().split('T')[0], // Today's date
    workDays: []
  };

  isEditMode = false;
  loading = false;
  error: string | null = null;
  
  // Enum references for template
  EmployeeType = EmployeeType;
  WorkDay = WorkDay;
  
  // Available work days
  availableWorkDays = Object.values(WorkDay);
  
  // Selected work days (for easier handling in template)
  selectedWorkDays: { [key in WorkDay]: boolean } = {
    [WorkDay.MONDAY]: false,
    [WorkDay.TUESDAY]: false,
    [WorkDay.WEDNESDAY]: false,
    [WorkDay.THURSDAY]: false,
    [WorkDay.FRIDAY]: false,
    [WorkDay.SATURDAY]: false,
    [WorkDay.SUNDAY]: false
  };

  constructor(
    private employeeService: EmployeeService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const matricule = this.route.snapshot.params['matricule'];
    if (matricule) {
      this.isEditMode = true;
      this.loadEmployee(matricule);
    }
  }

  loadEmployee(matricule: string): void {
    this.loading = true;
    this.employeeService.getEmployeeById(matricule).subscribe({
      next: (employee) => {
        this.employee = { ...employee };
        
        // Set selected work days
        if (employee.workDays) {
          employee.workDays.forEach(day => {
            this.selectedWorkDays[day] = true;
          });
        }
        
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Error loading employee: ' + error.message;
        this.loading = false;
      }
    });
  }

  onWorkDayChange(day: WorkDay, checked: boolean): void {
    this.selectedWorkDays[day] = checked;
    
    // Update the employee's work days array
    this.employee.workDays = Object.keys(this.selectedWorkDays)
      .filter(key => this.selectedWorkDays[key as WorkDay])
      .map(key => key as WorkDay);
  }

  onEmployeeTypeChange(): void {
    // Clear medical staff specific fields when changing to administration
    if (this.employee.employeeType === EmployeeType.ADMINISTRATION) {
      this.employee.specialite = '';
      this.employee.licenceNumber = '';
    }
  }

  onSubmit(): void {
    if (this.validateForm()) {
      this.loading = true;
      this.error = null;

      if (this.isEditMode) {
        this.employeeService.updateEmployee(this.employee.matricule, this.employee as Employee).subscribe({
          next: () => {
            this.router.navigate(['/employees']);
          },
          error: (error) => {
            this.error = 'Error updating employee: ' + error.message;
            this.loading = false;
          }
        });
      } else {
        this.employeeService.createEmployee(this.employee as Employee).subscribe({
          next: () => {
            this.router.navigate(['/employees']);
          },
          error: (error) => {
            this.error = 'Error creating employee: ' + error.message;
            this.loading = false;
          }
        });
      }
    }
  }

  validateForm(): boolean {
    if (!this.employee.matricule || !this.employee.nom || !this.employee.prenom || 
        !this.employee.poste || !this.employee.dateEmbauche) {
      this.error = 'Please fill in all required fields.';
      return false;
    }

    // Validate email format if provided
    if (this.employee.email && !this.isValidEmail(this.employee.email)) {
      this.error = 'Please enter a valid email address.';
      return false;
    }

    // Validate dates
    if (this.employee.dateNaissance && this.employee.dateEmbauche) {
      const birthDate = new Date(this.employee.dateNaissance);
      const hireDate = new Date(this.employee.dateEmbauche);
      
      if (birthDate >= hireDate) {
        this.error = 'Birth date must be before hire date.';
        return false;
      }
    }

    // Validate shift times if provided
    if (this.employee.shiftStart && this.employee.shiftEnd) {
      if (this.employee.shiftStart >= this.employee.shiftEnd) {
        this.error = 'Shift start time must be before shift end time.';
        return false;
      }
    }

    return true;
  }

  isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  cancel(): void {
    this.router.navigate(['/employees']);
  }

  isMedicalStaff(): boolean {
    return this.employee.employeeType === EmployeeType.MEDICAL_STAFF;
  }
}