import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatGridListModule } from '@angular/material/grid-list';

import { EmployeeService } from '../employees/employee.service';
import { PatientService } from '../patients/patient.service';
import { DoctorService } from '../doctors/doctor.service';
import { NurseService } from '../nurses/nurse.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatGridListModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  stats = {
    employees: 0,
    patients: 0,
    doctors: 0,
    nurses: 0
  };

  loading = false;

  constructor(
    private employeeService: EmployeeService,
    private patientService: PatientService,
    private doctorService: DoctorService,
    private nurseService: NurseService
  ) {}

  ngOnInit(): void {
    this.loadStatistics();
  }

  loadStatistics(): void {
    this.loading = true;

    // Load all statistics in parallel
    this.employeeService.getAllEmployees().subscribe({
      next: (data) => {
        this.stats.employees = data.length;
        this.checkLoadingComplete();
      },
      error: () => {
        this.checkLoadingComplete();
      }
    });

    this.patientService.getAllPatients().subscribe({
      next: (data) => {
        this.stats.patients = data.length;
        this.checkLoadingComplete();
      },
      error: () => {
        this.checkLoadingComplete();
      }
    });

    this.doctorService.getAllDoctors().subscribe({
      next: (data) => {
        this.stats.doctors = data.length;
        this.checkLoadingComplete();
      },
      error: () => {
        this.checkLoadingComplete();
      }
    });

    this.nurseService.getAllNurses().subscribe({
      next: (data) => {
        this.stats.nurses = data.length;
        this.checkLoadingComplete();
      },
      error: () => {
        this.checkLoadingComplete();
      }
    });
  }

  private checkLoadingComplete(): void {
    // Simple check - in production, use a counter or forkJoin
    setTimeout(() => {
      this.loading = false;
    }, 1000);
  }
}
