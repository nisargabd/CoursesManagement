import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class RoleService {
  getRole() {
    return localStorage.getItem('role');
  }
  isAdmin() {
    return this.getRole() === 'ADMIN';
  }
  isUser() {
    return this.getRole() === 'USER';
  }
}
