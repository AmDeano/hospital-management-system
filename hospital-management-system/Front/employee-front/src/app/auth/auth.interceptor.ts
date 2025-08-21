// src/app/auth/auth.interceptor.ts
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';
import { TokenService } from './token.service';
import { catchError, switchMap, throwError } from 'rxjs';

let refreshing = false;

export const AuthInterceptor: HttpInterceptorFn = (req, next) => {
  const tokens = inject(TokenService);
  const auth = inject(AuthService);

  const withAuth = tokens.access ? req.clone({ setHeaders: { Authorization: `Bearer ${tokens.access}` } }) : req;

  return next(withAuth).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 401 && tokens.refresh && !refreshing) {
        refreshing = true;
        return auth.refresh().pipe(
          switchMap(() => {
            refreshing = false;
            const retried = tokens.access ? req.clone({ setHeaders: { Authorization: `Bearer ${tokens.access}` } }) : req;
            return next(retried);
          }),
          catchError(e => { refreshing = false; tokens.clear(); return throwError(() => e); })
        );
      }
      return throwError(() => err);
    })
  );
};
