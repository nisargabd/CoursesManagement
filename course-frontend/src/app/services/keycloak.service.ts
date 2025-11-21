// src/app/keycloak.service.ts
import { Injectable } from '@angular/core';
import Keycloak, { KeycloakInstance } from 'keycloak-js';

@Injectable({ providedIn: 'root' })
export class KeycloakService {
  private keycloak!: KeycloakInstance;

  init(): Promise<boolean> {
    this.keycloak = new Keycloak({
      url: 'http://localhost:8080',
      realm: 'CourseManagement',
      clientId: 'course-angular',
    });

    return this.keycloak
      .init({
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri: window.location.origin + '/assets/silent-check-sso.html',
        checkLoginIframe: false
      })
      .then(authenticated => {
        console.log('Keycloak init result:', authenticated);
        if (authenticated) {
          this.syncLocalStorage();
          this.startTokenRefresh();
        } else {
          console.log('User is NOT authenticated (Public Mode)');
          localStorage.clear();
        }
        return authenticated;
      })
      .catch(err => {
        console.error('Keycloak initialization error:', err);
        return false;
      });
  }

  private startTokenRefresh() {
    setInterval(() => {
      this.keycloak
        .updateToken(30) 
        .then(refreshed => {
          if (refreshed) {
            this.syncLocalStorage();
          }
        })
        .catch(err => console.error('Token refresh failed', err));
    }, 30000);
  }

  private syncLocalStorage() {
    console.log('Syncing to localStorage. Token present:', !!this.keycloak.token);
    if (this.keycloak.token) {
      localStorage.setItem('token', this.keycloak.token);
    }

    const tokenParsed = this.keycloak.tokenParsed as any;
    if (tokenParsed) {
      const username = tokenParsed.preferred_username || tokenParsed.name || '';
      localStorage.setItem('username', username);

      const roles = tokenParsed.realm_access?.roles || [];
      console.log('Keycloak roles:', roles);
      if (roles.includes('ADMIN') || roles.includes('admin')) {
        localStorage.setItem('role', 'ADMIN');
      } else {
        localStorage.setItem('role', 'USER');
      }
    }
  }

  getToken(): string | undefined {
    return this.keycloak?.token;
  }

  login() {
    this.keycloak.login();
  }

  logout() {
    localStorage.clear();
    return this.keycloak.logout({
      redirectUri: window.location.origin + '/login',
    });
  }

  getKeycloakInstance() {
    return this.keycloak;
  }
}
