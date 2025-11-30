import { Component, signal, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { Router, RouterLink } from '@angular/router';
import { PatientAuthService } from '../../auth/patient-auth.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-patient-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSnackBarModule,
    RouterLink
  ],
  template: `
    <div class="register-container">
      <h2 style="margin:4px 0 16px; font-weight:700">Patient Registration</h2>
      <p class="mini">Create your patient account</p>

      <form [formGroup]="form" (ngSubmit)="submit()" style="margin-top:12px;">
        <mat-form-field appearance="outline" class="w-100">
          <mat-label>First Name</mat-label>
          <input matInput formControlName="firstName" autocomplete="given-name">
        </mat-form-field>

        <mat-form-field appearance="outline" class="w-100">
          <mat-label>Last Name</mat-label>
          <input matInput formControlName="lastName" autocomplete="family-name">
        </mat-form-field>

        <mat-form-field appearance="outline" class="w-100">
          <mat-label>Email</mat-label>
          <input matInput type="email" formControlName="email" autocomplete="email">
        </mat-form-field>

        <mat-form-field appearance="outline" class="w-100">
          <mat-label>Password</mat-label>
          <input matInput [type]="hide() ? 'password':'text'" formControlName="password" autocomplete="new-password">
          <button mat-icon-button matSuffix type="button" (click)="toggle()" [attr.aria-label]="'Toggle password'">
            <mat-icon>{{ hide() ? 'visibility' : 'visibility_off' }}</mat-icon>
          </button>
        </mat-form-field>

        <mat-form-field appearance="outline" class="w-100">
          <mat-label>Date of Birth</mat-label>
          <input matInput [matDatepicker]="picker" formControlName="dateNaissance">
          <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
          <mat-datepicker #picker></mat-datepicker>
        </mat-form-field>

        <mat-form-field appearance="outline" class="w-100">
          <mat-label>CIN (Optional for minors)</mat-label>
          <input matInput formControlName="cin">
        </mat-form-field>

        <mat-form-field appearance="outline" class="w-100">
          <mat-label>Phone Number</mat-label>
          <input matInput formControlName="numeroTelephone" type="tel">
        </mat-form-field>

        <mat-form-field appearance="outline" class="w-100">
          <mat-label>Address</mat-label>
          <input matInput formControlName="adresse">
        </mat-form-field>

        <mat-form-field appearance="outline" class="w-100">
          <mat-label>Social Security Number</mat-label>
          <input matInput formControlName="numeroSecuriteSociale">
        </mat-form-field>

        <mat-form-field appearance="outline" class="w-100">
          <mat-label>Parent CIN (if minor)</mat-label>
          <input matInput formControlName="parentCin">
        </mat-form-field>

        <button mat-raised-button color="primary" class="w-100" [disabled]="form.invalid || loading()">
          {{ loading() ? 'Creating account...' : 'Create Patient Account' }}
        </button>
      </form>

      <div style="margin-top:14px" class="mini">
        Already have an account?
        <a routerLink="/login" class="link">Sign in</a>
      </div>
    </div>
  `,
  styles: [`
    .register-container { max-width: 500px; margin: 0 auto; padding: 20px; }
    .w-100 { width: 100%; margin-bottom: 12px; }
    .mini { font-size: 0.9em; color: #666; }
    .link { color: #1976d2; text-decoration: none; font-weight: 500; cursor: pointer; }
    .link:hover { text-decoration: underline; }
  `]
})
export class PatientRegisterComponent {
  private fb = inject(FormBuilder);
  private patientAuth = inject(PatientAuthService);
  private router = inject(Router);
  private sb = inject(MatSnackBar);

  hide = signal(true);
  loading = signal(false);

  form = this.fb.group({
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    dateNaissance: ['', [Validators.required]],
    cin: [''],
    numeroTelephone: [''],
    adresse: [''],
    numeroSecuriteSociale: [''],
    parentCin: ['']
  });

  toggle() { this.hide.update(v => !v); }

  submit() {
    if (this.form.invalid) return;
    this.loading.set(true);
    const formData = this.form.value as any;
    if (formData.dateNaissance instanceof Date) {
      formData.dateNaissance = formData.dateNaissance.toISOString().split('T')[0];
    }
    this.patientAuth.registerPatient(formData).subscribe({
      next: () => {
        this.loading.set(false);
        this.sb.open('Patient account created successfully!', 'Close', { duration: 3000 });
        this.router.navigateByUrl('/login');
      },
      error: (err) => {
        this.loading.set(false);
        const errorMessage = err.error?.message || 'Registration failed. Please try again.';
        this.sb.open(errorMessage, 'Close', { duration: 5000 });
      }
    });
  }
}
