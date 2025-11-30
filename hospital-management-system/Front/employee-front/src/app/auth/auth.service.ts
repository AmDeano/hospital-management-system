// src/app/auth/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { TokenService } from './token.service';
import { ConfigService } from '../core/config.service';

export interface AuthResponse {
  accessToken: string; refreshToken: string; tokenType: string;
  expiresAt: string; roles: string[]; userId: number; username: string; email: string;
}

@Injectable({ providedIn:'root' })
export class AuthService {
  private base: string;

  constructor(
    private http: HttpClient,
    private tokens: TokenService,
    private config: ConfigService
  ) {
    this.base = this.config.getAuthServiceUrl();
  }

  login(matricule: string, password: string){
    return this.http.post<AuthResponse>(`${this.base}/employee/login`, { matricule, password })
      .pipe(tap(res => { this.tokens.access = res.accessToken; this.tokens.refresh = res.refreshToken; }));
  }

  register(data: {matricule:string; email:string; password:string; roles:string[]; externalId?:string}){
    return this.http.post<void>(`${this.base}/employee/register`, data);
  }

  refresh(){
    return this.http.post<AuthResponse>(`${this.base}/refresh?refreshToken=${this.tokens.refresh}`, {})
      .pipe(tap(res => { this.tokens.access = res.accessToken; }));
  }

  logout(){ this.tokens.clear(); }

  /**
   * Get user roles from the JWT token
   */
  getUserRoles(): string[] {
    return this.tokens.roles();
  }
}
