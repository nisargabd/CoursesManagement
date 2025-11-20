import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { KeycloakService } from '../services/keycloak.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router,
    private keycloakService: KeycloakService
  ) { }

  canActivate(): boolean {
    if (this.authService.isLoggedIn()) {
      console.log('AuthGuard: User is logged in');
      return true;
    } else {
      console.log('AuthGuard: User is NOT logged in, redirecting to Keycloak login');
      this.keycloakService.login();
      return false;
    }
  }
}
