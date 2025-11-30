// src/app/auth/token.service.ts
import { Injectable } from '@angular/core';

type Decoded = { exp?: number; roles?: string[]; sub?: string; uid?: number; email?: string };

@Injectable({ providedIn: 'root' })
export class TokenService {
  private readonly ACCESS = 'accessToken';
  private readonly REFRESH = 'refreshToken';

  get access(): string | null { return localStorage.getItem(this.ACCESS); }
  set access(v: string | null) { v ? localStorage.setItem(this.ACCESS, v) : localStorage.removeItem(this.ACCESS); }

  get refresh(): string | null { return localStorage.getItem(this.REFRESH); }
  set refresh(v: string | null) { v ? localStorage.setItem(this.REFRESH, v) : localStorage.removeItem(this.REFRESH); }

  clear(){ this.access = null; this.refresh = null; }

  logout(): void { this.clear(); }

  isLoggedIn(): boolean { return !!this.access && !this.isExpired(this.access!); }

  decode<T = Decoded>(token: string | null): T | null {
    if(!token) return null;
    const parts = token.split('.');
    if(parts.length !== 3) return null;
    try { return JSON.parse(atob(parts[1])); } catch { return null; }
  }

  isExpired(token: string): boolean {
    const d = this.decode(token) as Decoded | null;
    if(!d?.exp) return false;
    return Date.now()/1000 >= d.exp;
  }

  roles(): string[] { return (this.decode(this.access)?.roles as string[]|undefined) ?? []; }
  username(): string | null { return (this.decode(this.access)?.sub as string|undefined) ?? null; }
}
