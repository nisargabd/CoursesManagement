import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const expectedRoles = route.data['roles'] as string[];
    const role = localStorage.getItem('role');

    if (!expectedRoles.includes(role || '')) {
      this.router.navigate(['/unauthorized']);
      return false;
    }
    return true;
  }
}
