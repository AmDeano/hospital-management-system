// src/app/auth/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { TokenService } from './token.service';

export interface AuthResponse {
  accessToken: string; refreshToken: string; tokenType: string;
  expiresAt: string; roles: string[]; userId: number; username: string; email: string;
}

@Injectable({ providedIn:'root' })
export class AuthService {
  private base = 'http://localhost:9000/auth-service/api/auth';

  constructor(private http: HttpClient, private tokens: TokenService) {}

  login(username: string, password: string){
    return this.http.post<AuthResponse>(`${this.base}/login`, { username, password })
      .pipe(tap(res => { this.tokens.access = res.accessToken; this.tokens.refresh = res.refreshToken; }));
  }

  register(data: {username:string; email:string; password:string; roles:string[]; externalId?:string}){
    return this.http.post<void>(`${this.base}/register`, data);
  }

  refresh(){
    return this.http.post<AuthResponse>(`${this.base}/refresh?refreshToken=${this.tokens.refresh}`, {})
      .pipe(tap(res => { this.tokens.access = res.accessToken; }));
  }

  logout(){ this.tokens.clear(); }
}
