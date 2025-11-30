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

import { NurseService } from '../nurse.service';
import { Nurse } from '../nurse.model';

@Component({
  selector: 'app-nurse-list',
  standalone: true,
  imports: [
    CommonModule, FormsModule, RouterLink, MatTableModule, MatButtonModule,
    MatIconModule, MatInputModule, MatFormFieldModule, MatSnackBarModule
  ],
  templateUrl: './nurse-list.component.html',
  styleUrls: ['./nurse-list.component.css']
})
export class NurseListComponent implements OnInit {

  nurses: Nurse[] = [];
  displayedColumns: string[] = ['matricule', 'firstName', 'shift', 'isActive', 'actions'];
  loading = false;
  shift = '';

  constructor(
    private nurseService: NurseService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadNurses();
  }

  loadNurses(): void {
    this.loading = true;
    this.nurseService.getAllNurses().subscribe({
      next: (data) => {
        this.nurses = data;
        this.loading = false;
      },
      error: (error) => {
        this.snackBar.open('Failed to load nurses', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  filterByShift(): void {
    if (!this.shift || this.shift.trim() === '') {
      this.loadNurses();
      return;
    }

    this.loading = true;
    this.nurseService.getNursesByShift(this.shift).subscribe({
      next: (data) => {
        this.nurses = data;
        this.loading = false;
      },
      error: (error) => {
        this.snackBar.open('Failed to filter nurses', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  loadActiveByShift(): void {
    if (!this.shift || this.shift.trim() === '') {
      this.snackBar.open('Please enter a shift', 'Close', { duration: 3000 });
      return;
    }

    this.loading = true;
    this.nurseService.getActiveNursesByShift(this.shift).subscribe({
      next: (data) => {
        this.nurses = data;
        this.loading = false;
      },
      error: (error) => {
        this.snackBar.open('Failed to load active nurses', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  deleteNurse(id: number): void {
    if (confirm('Are you sure you want to delete this nurse?')) {
      this.nurseService.deleteNurse(id).subscribe({
        next: () => {
          this.snackBar.open('Nurse deleted successfully', 'Close', { duration: 3000 });
          this.loadNurses();
        },
        error: (error) => {
          this.snackBar.open('Failed to delete nurse', 'Close', { duration: 3000 });
        }
      });
    }
  }
}
