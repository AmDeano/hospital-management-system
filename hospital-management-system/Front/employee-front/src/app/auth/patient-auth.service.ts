// src/app/auth/patient-auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { TokenService } from './token.service';
import { ConfigService } from '../core/config.service';

export interface PatientRegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  cin?: string;
  dateNaissance: string;
  numeroTelephone?: string;
  adresse?: string;
  numeroSecuriteSociale?: string;
  parentCin?: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresAt: string;
  roles: string[];
  userId: number;
  username: string;
  email: string;
}

@Injectable({ providedIn: 'root' })
export class PatientAuthService {
  private base: string;

  constructor(
    private http: HttpClient,
    private tokens: TokenService,
    private config: ConfigService
  ) {
    this.base = this.config.getAuthServiceUrl();
  }

  /**
   * Register a new patient (adult or minor)
   */
  registerPatient(data: PatientRegisterRequest) {
    return this.http.post<void>(`${this.base}/patient/register`, data);
  }

  /**
   * Patient login with email and password
   */
  loginPatient(email: string, password: string) {
    return this.http.post<AuthResponse>(`${this.base}/patient/login`, { email, password })
      .pipe(tap(res => {
        this.tokens.access = res.accessToken;
        this.tokens.refresh = res.refreshToken;
      }));
  }

  /**
   * Refresh access token
   */
  refresh() {
    return this.http.post<AuthResponse>(`${this.base}/refresh?refreshToken=${this.tokens.refresh}`, {})
      .pipe(tap(res => { this.tokens.access = res.accessToken; }));
  }

  /**
   * Logout
   */
  logout() {
    this.tokens.clear();
  }
}
