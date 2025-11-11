    import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private api = environment.baseUrl;

  constructor(private http: HttpClient) {}

  login(payload: any) {
    return this.http.post(`${this.api}/auth/login`, payload).pipe(
      tap((res: any) => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('role', res.role);
        localStorage.setItem('username', res.username);
      })
    );
  }

  register(payload: any) {
    return this.http.post(`${this.api}/auth/register`, payload);
  }

  logout() {
    localStorage.clear();
  }

  isLoggedIn() {
    return !!localStorage.getItem('token');
  }

  getRole() {
    return localStorage.getItem('role');
  }

  getUsername() {
    return localStorage.getItem('username');
  }
  isAdmin() {
  return this.getRole() === 'ADMIN';
}

}
