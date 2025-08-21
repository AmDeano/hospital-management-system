import { Component, signal, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    ReactiveFormsModule, NgIf,
    MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule, MatSnackBarModule,
    RouterLink
  ],
  template: `
    <h2 style="margin:4px 0 16px; font-weight:700">Create Account</h2>
    <p class="mini">Sign up to get started</p>

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

      <button mat-raised-button color="primary" class="w-100" [disabled]="form.invalid || loading()">
        {{ loading() ? 'Creating account...' : 'Create account' }}
      </button>
    </form>

    <div style="margin-top:14px" class="mini">
      Already have an account?
      <a routerLink="/login" class="link">Sign in</a>
    </div>
  `,
  styles: [`.w-100{width:100%}`]
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  private sb = inject(MatSnackBar);

  hide = signal(true);
  loading = signal(false);

  form = this.fb.group({
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(4)]],
  });

  toggle() { 
    this.hide.update(v => !v); 
  }

  submit() {
    if (this.form.invalid) return;
    this.loading.set(true);
    const formData = this.form.value as any;
    this.auth.register(formData).subscribe({
      next: () => { 
        this.loading.set(false); 
        this.sb.open('Account created successfully!', 'Close', { duration: 3000 });
        this.router.navigateByUrl('/login'); 
      },
      error: () => { 
        this.loading.set(false); 
        this.sb.open('Registration failed. Please try again.', 'Close', { duration: 3000 }); 
      }
    });
  }
}