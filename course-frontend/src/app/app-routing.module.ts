import { Routes } from '@angular/router';

import { CourseListComponent } from './components/course-list/course-list.component';
import { CourseDetailComponent } from './components/course-detail/course-detail.component';
import { CourseFormComponent } from './components/course-form/course-form.component';
import { UnitFormComponent } from './components/unit-form/unit-form.component';

import { RegisterComponent } from './components/register/register.component';

import { AuthGuard } from './guards/auth.guard';
import { LoginComponent } from './components/login/login.component';
import { AdminGuard } from './guards/admin.guard';

export const routes: Routes = [

  // ✅ PUBLIC ROUTES FIRST
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  // ✅ PROTECTED ROUTES
  {
    path: 'courses',
    canActivate: [AuthGuard],
    children: [
      { path: '', component: CourseListComponent },
      { path: 'view/:id', component: CourseDetailComponent },
      { path: 'edit/:id', component: CourseFormComponent },
      { path: 'add', component: CourseFormComponent },
      { path: 'unit', component: UnitFormComponent }
      
    ]
  },
  {
  path: 'add-course',
  component: CourseListComponent,
  canActivate: [AdminGuard]
},
{
  path: 'edit-course/:id',
  component: CourseListComponent,
  canActivate: [AdminGuard]
},

{ path: 'admin-dashboard', redirectTo: 'courses', pathMatch: 'full' },
{ path: 'user-dashboard', redirectTo: 'courses', pathMatch: 'full' },

  // ✅ DEFAULT ROUTE
  { path: '', redirectTo: 'courses', pathMatch: 'full' },

  // ✅ WILDCARD LAST (important)
  { path: '**', redirectTo: 'courses' }
];
