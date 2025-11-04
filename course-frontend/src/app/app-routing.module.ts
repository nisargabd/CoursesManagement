import { Routes } from '@angular/router';
import { CourseListComponent } from './components/course-list/course-list.component';
import { CourseFormComponent } from './components/course-form/course-form.component';
import { CourseDetailComponent } from './components/course-detail/course-detail.component';
import { UnitFormComponent } from './components/unit-form/unit-form.component';

export const routes: Routes = [
  { path: '', redirectTo: '/courses', pathMatch: 'full' },
  { path: 'courses', component: CourseListComponent },
  { path: 'courses/view/:id', component: CourseDetailComponent },
  { path: 'courses/edit/:id', component: CourseFormComponent },
  { path: 'courses/add', component: CourseFormComponent },
  { path: 'createCourse', redirectTo: '/courses/add' },
  { path: 'createUnit', component: UnitFormComponent },
  { path: '**', redirectTo: '/courses' }
];
