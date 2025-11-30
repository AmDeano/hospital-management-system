import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';

import { PatientService } from '../patient.service';
import { Patient, PatientSearchFilters } from '../patient.model';

@Component({
  selector: 'app-patient-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatSnackBarModule
  ],
  templateUrl: './patient-list.component.html',
  styleUrls: ['./patient-list.component.css']
})
export class PatientListComponent implements OnInit {

  patients: Patient[] = [];
  displayedColumns: string[] = ['id', 'nom', 'email', 'isMinor', 'actions'];
  loading = false;
  searchFilters: PatientSearchFilters = {};

  constructor(
    private patientService: PatientService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadPatients();
  }

  loadPatients(): void {
    this.loading = true;
    this.patientService.getAllPatients().subscribe({
      next: (data) => {
        this.patients = data;
        this.loading = false;
      },
      error: (error) => {
        this.snackBar.open('Failed to load patients', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  searchPatients(): void {
    if (!this.searchFilters.name || this.searchFilters.name.trim() === '') {
      this.loadPatients();
      return;
    }

    this.loading = true;
    this.patientService.searchPatientsByName(this.searchFilters.name).subscribe({
      next: (data) => {
        this.patients = data;
        this.loading = false;
      },
      error: (error) => {
        this.snackBar.open('Failed to search patients', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  deletePatient(id: string): void {
    if (confirm('Are you sure you want to delete this patient?')) {
      this.patientService.deletePatient(id).subscribe({
        next: () => {
          this.snackBar.open('Patient deleted successfully', 'Close', { duration: 3000 });
          this.loadPatients();
        },
        error: (error) => {
          this.snackBar.open('Failed to delete patient', 'Close', { duration: 3000 });
        }
      });
    }
  }

  loadMinors(): void {
    this.loading = true;
    this.patientService.getAllMinors().subscribe({
      next: (data) => {
        this.patients = data;
        this.loading = false;
      },
      error: (error) => {
        this.snackBar.open('Failed to load minors', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }
}
