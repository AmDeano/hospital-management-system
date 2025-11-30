import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTabsModule } from '@angular/material/tabs';
import { MatListModule } from '@angular/material/list';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { DoctorService } from '../doctor.service';
import { DoctorDashboard, DoctorSchedule, DoctorAvailabilitySlot } from '../doctor.model';

@Component({
  selector: 'app-doctor-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatToolbarModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatTabsModule,
    MatListModule,
    MatGridListModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './doctor-dashboard.component.html',
  styleUrls: ['./doctor-dashboard.component.scss']
})
export class DoctorDashboardComponent implements OnInit {
  doctorMatricule: string = '';
  dashboard: DoctorDashboard | null = null;
  loading: boolean = true;
  error: string | null = null;

  constructor(
    private doctorService: DoctorService,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.doctorMatricule = params['doctorMatricule'];
      if (this.doctorMatricule) {
        this.loadDoctorDashboard();
      }
    });
  }

  loadDoctorDashboard(): void {
    this.loading = true;
    this.error = null;

    this.doctorService.getDoctorDashboard(this.doctorMatricule).subscribe({
      next: (data) => {
        this.dashboard = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading doctor dashboard:', error);
        this.error = 'Failed to load doctor dashboard';
        this.loading = false;
        this.snackBar.open('Error loading dashboard', 'Close', { duration: 5000 });
      }
    });
  }

  // Helper method to format time
  formatTime(time: string): string {
    if (!time) return '';
    return time.substring(0, 5); // HH:MM
  }

  // Helper method to format date
  formatDate(date: string): string {
    if (!date) return '';
    return new Date(date).toLocaleDateString();
  }

  // Get weekday name from enum
  getWeekdayName(dayOfWeek: string): string {
    const dayMap: { [key: string]: string } = {
      'MONDAY': 'Monday',
      'TUESDAY': 'Tuesday',
      'WEDNESDAY': 'Wednesday',
      'THURSDAY': 'Thursday',
      'FRIDAY': 'Friday',
      'SATURDAY': 'Saturday',
      'SUNDAY': 'Sunday'
    };
    return dayMap[dayOfWeek] || dayOfWeek;
  }

  // Navigation methods for different features
  viewPrescriptions(): void {
    this.snackBar.open('Prescriptions feature - Coming soon', 'Close', { duration: 3000 });
  }

  viewMedicalRecords(): void {
    this.snackBar.open('Medical Records feature - Coming soon', 'Close', { duration: 3000 });
  }

  viewAppointments(): void {
    this.snackBar.open('Appointments feature - Coming soon', 'Close', { duration: 3000 });
  }

  orderTests(): void {
    this.snackBar.open('Test Ordering feature - Coming soon', 'Close', { duration: 3000 });
  }

  viewPatients(): void {
    this.snackBar.open('Patient List feature - Coming soon', 'Close', { duration: 3000 });
  }

  manageSchedule(): void {
    this.snackBar.open('Schedule Management feature - Coming soon', 'Close', { duration: 3000 });
  }
}
