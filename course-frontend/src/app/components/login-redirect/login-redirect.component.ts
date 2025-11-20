import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { KeycloakService } from '../../services/keycloak.service';

@Component({
    selector: 'app-login-redirect',
    standalone: true,
    template: '<div style="display: flex; justify-content: center; align-items: center; height: 100vh;">Redirecting to login...</div>'
})
export class LoginRedirectComponent implements OnInit {
    constructor(private keycloak: KeycloakService, private router: Router) { }

    ngOnInit() {
        if (this.keycloak.getToken()) {
            this.router.navigate(['/courses']);
        } else {
            this.keycloak.login();
        }
    }
}
