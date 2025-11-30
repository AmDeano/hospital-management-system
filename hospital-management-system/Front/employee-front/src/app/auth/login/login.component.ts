import { Component, signal, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule, MatSnackBarModule,
    RouterLink
  ],
  template: `
    <h2 style="margin:4px 0 16px; font-weight:700">Welcome back</h2>
    <p class="mini">Sign in with your credentials</p>

    <form [formGroup]="form" (ngSubmit)="submit()" style="margin-top:12px;">
      <mat-form-field appearance="outline" class="w-100">
        <mat-label>Matricule</mat-label>
        <input matInput formControlName="matricule" autocomplete="username">
      </mat-form-field>

      <mat-form-field appearance="outline" class="w-100">
        <mat-label>Password</mat-label>
        <input matInput [type]="hide() ? 'password':'text'" formControlName="password" autocomplete="current-password">
        <button mat-icon-button matSuffix type="button" (click)="toggle()" [attr.aria-label]="'Toggle password'">
          <mat-icon>{{ hide() ? 'visibility' : 'visibility_off' }}</mat-icon>
        </button>
      </mat-form-field>

      <button mat-raised-button color="primary" class="w-100" [disabled]="form.invalid || loading()">
        {{ loading() ? 'Signing in...' : 'Sign in' }}
      </button>
    </form>

    <div style="margin-top:14px" class="mini">
      No account?
      <a routerLink="/register" class="link">Create one</a>
    </div>
  `,
  styles: [`.w-100{width:100%}`]
})
export class LoginComponent implements OnInit {
  hide = signal(true);
  loading = signal(false);
  form!: FormGroup;

  constructor(
    private fb: FormBuilder, 
    private auth: AuthService, 
    private router: Router, 
    private sb: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      matricule: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(4)]],
    });
  }

  toggle() { 
    this.hide.update(v => !v); 
  }

  submit() {
    if (this.form.invalid) return;
    this.loading.set(true);
    const { matricule, password } = this.form.value as any;
    this.auth.login(matricule, password).subscribe({
      next: () => {
        this.loading.set(false);
        // Get user roles from token and redirect accordingly
        const roles = this.auth.getUserRoles();
        if (roles.includes('DOCTOR')) {
          this.router.navigateByUrl(`/dashboard/doctors/${matricule}`);
        } else {
          this.router.navigateByUrl('/employees');
        }
      },
      error: (err) => {
        this.loading.set(false);
        const errorMessage = err.error?.message || 'Invalid matricule or password';
        this.sb.open(errorMessage, 'Close', { duration: 3000 });
      }
    });
  }
}
