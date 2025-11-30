import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { DoctorService } from '../doctor.service';
import { Doctor } from '../doctor.model';

@Component({
  selector: 'app-doctor-list',
  standalone: true,
  imports: [
    CommonModule, FormsModule, RouterLink, MatTableModule, MatButtonModule,
    MatIconModule, MatInputModule, MatFormFieldModule, MatSnackBarModule
  ],
  templateUrl: './doctor-list.component.html',
  styleUrls: ['./doctor-list.component.css']
})
export class DoctorListComponent implements OnInit {

  doctors: Doctor[] = [];
  displayedColumns: string[] = ['matricule', 'firstName', 'specialization', 'isActive', 'actions'];
  loading = false;
  specialization = '';

  constructor(
    private doctorService: DoctorService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadDoctors();
  }

  loadDoctors(): void {
    this.loading = true;
    this.doctorService.getAllDoctors().subscribe({
      next: (data) => {
        this.doctors = data;
        this.loading = false;
      },
      error: (error) => {
        this.snackBar.open('Failed to load doctors', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  filterBySpecialization(): void {
    if (!this.specialization || this.specialization.trim() === '') {
      this.loadDoctors();
      return;
    }

    this.loading = true;
    this.doctorService.getDoctorsBySpecialization(this.specialization).subscribe({
      next: (data) => {
        this.doctors = data;
        this.loading = false;
      },
      error: (error) => {
        this.snackBar.open('Failed to filter doctors', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  loadAvailable(): void {
    this.loading = true;
    this.doctorService.getAvailableDoctors().subscribe({
      next: (data) => {
        this.doctors = data;
        this.loading = false;
      },
      error: (error) => {
        this.snackBar.open('Failed to load available doctors', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  deleteDoctor(id: number): void {
    if (confirm('Are you sure you want to delete this doctor?')) {
      this.doctorService.deleteDoctor(id).subscribe({
        next: () => {
          this.snackBar.open('Doctor deleted successfully', 'Close', { duration: 3000 });
          this.loadDoctors();
        },
        error: (error) => {
          this.snackBar.open('Failed to delete doctor', 'Close', { duration: 3000 });
        }
      });
    }
  }
}
