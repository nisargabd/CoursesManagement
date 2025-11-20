import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  {
    path: 'courses',
    loadComponent: () => import('./components/course-list/course-list.component').then(m => m.CourseListComponent)
    // Removed AuthGuard to allow public access
  },
  {
    path: 'courses/add',
    loadComponent: () => import('./components/course-form/course-form.component').then(m => m.CourseFormComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'courses/view/:id',
    loadComponent: () => import('./components/course-detail/course-detail.component').then(m => m.CourseDetailComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'courses/edit/:id',
    loadComponent: () => import('./components/course-form/course-form.component').then(m => m.CourseFormComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'login',
    loadComponent: () => import('./components/login-redirect/login-redirect.component').then(m => m.LoginRedirectComponent)
  },
  // Removed login and register routes
  { path: '**', redirectTo: '/courses' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
