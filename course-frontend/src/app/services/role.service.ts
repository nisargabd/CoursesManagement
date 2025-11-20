import { Injectable } from '@angular/core';
import { KeycloakService } from './keycloak.service';

@Injectable({ providedIn: 'root' })
export class RoleService {
  constructor(private keycloakService: KeycloakService) { }

  getRole() {
    return localStorage.getItem('role');
  }
  isAdmin() {
    return this.getRole() === 'ADMIN';
  }
  isUser() {
    return this.getRole() === 'USER';
  }

  login() {
    this.keycloakService.login();
  }

  logout() {
    this.keycloakService.logout();
  }
}
