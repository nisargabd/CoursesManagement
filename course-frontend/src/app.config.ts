// src/app/app.config.ts
import { ApplicationConfig, APP_INITIALIZER } from '@angular/core';
import { provideRouter } from '@angular/router';
import {
  provideHttpClient,
  withInterceptors,
} from '@angular/common/http';
import { KeycloakService } from './app/services/keycloak.service';
import { authInterceptor } from './app/services/auth.interceptor';
import { routes } from './app/app-routing.module';

export function initKeycloak(keycloak: KeycloakService) {
  return () => keycloak.init(); // Angular waits for this Promise
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),

    {
      provide: APP_INITIALIZER,
      useFactory: initKeycloak,
      deps: [KeycloakService],
      multi: true,
    },
  ],
};
