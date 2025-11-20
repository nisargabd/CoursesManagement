// src/app/auth.interceptor.ts
import { inject } from '@angular/core';
import {
  HttpInterceptorFn,
  HttpRequest,
  HttpHandlerFn,
} from '@angular/common/http';
import { KeycloakService } from './keycloak.service';

export const authInterceptor: HttpInterceptorFn = (
  req: HttpRequest<any>,
  next: HttpHandlerFn
) => {
  const keycloak = inject(KeycloakService);
  const token = keycloak.getToken();

  // Do not attach token when calling Keycloak itself
  if (!token || req.url.startsWith('http://localhost:8080')) {
    return next(req);
  }

  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`,
    },
  });

  return next(authReq);
};
