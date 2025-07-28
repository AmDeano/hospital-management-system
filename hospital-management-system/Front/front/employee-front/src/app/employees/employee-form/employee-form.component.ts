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
    // Remove the default date to avoid validation issues
    dateEmbauche: '',
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

  onWorkDayChange(day: WorkDay, event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;
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
            this.handleServerError(error);
          }
        });
      } else {
        this.employeeService.createEmployee(this.employee as Employee).subscribe({
          next: () => {
            this.router.navigate(['/employees']);
          },
          error: (error) => {
            this.handleServerError(error);
          }
        });
      }
    }
  }

  private handleServerError(error: any): void {
    this.loading = false;
    
    // Handle validation errors from the server
    if (error.status === 400 && error.error) {
      if (error.error.message) {
        this.error = error.error.message;
      } else if (error.error.errors) {
        // Handle field validation errors
        const fieldErrors = error.error.errors;
        const errorMessages = fieldErrors.map((err: any) => err.defaultMessage).join(', ');
        this.error = errorMessages;
      } else {
        this.error = 'Validation failed. Please check your input.';
      }
    } else {
      this.error = 'Error saving employee: ' + (error.message || 'Unknown error occurred');
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

    // Validate that hire date is not in the future
    const hireDate = new Date(this.employee.dateEmbauche);
    const today = new Date();
    today.setHours(23, 59, 59, 999); // Set to end of today to allow today's date
    
    if (hireDate > today) {
      this.error = 'Hire date cannot be in the future.';
      return false;
    }

    // Validate dates relationship
    if (this.employee.dateNaissance && this.employee.dateEmbauche) {
      const birthDate = new Date(this.employee.dateNaissance);
      const hireDateValidation = new Date(this.employee.dateEmbauche);
      
      if (birthDate >= hireDateValidation) {
        this.error = 'Birth date must be before hire date.';
        return false;
      }
      
      // Check if person is at least 16 years old at hire date
      const minimumAge = new Date(birthDate);
      minimumAge.setFullYear(minimumAge.getFullYear() + 16);
      
      if (hireDateValidation < minimumAge) {
        this.error = 'Employee must be at least 16 years old at hire date.';
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

  // Helper method to get maximum date for hire date input (today)
  getMaxHireDate(): string {
    return new Date().toISOString().split('T')[0];
  }

  // Helper method to get maximum date for birth date (must be at least 16 years ago)
  getMaxBirthDate(): string {
    const maxDate = new Date();
    maxDate.setFullYear(maxDate.getFullYear() - 16);
    return maxDate.toISOString().split('T')[0];
  }
}