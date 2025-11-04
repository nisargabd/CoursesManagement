import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatChipsModule } from '@angular/material/chips';
import { CourseService } from '../../services/course.service';
import { Course } from '../../models/course.model';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-course-detail',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatDialogModule,
    MatChipsModule,
    ConfirmDialogComponent
  ],
  templateUrl: './course-detail.component.html',
  styleUrls: ['./course-detail.component.scss']
})
export class CourseDetailComponent implements OnInit {
  course: Course | null = null;
  loading = true;
  error = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private courseService: CourseService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    const courseId = this.route.snapshot.paramMap.get('id');
    console.log('CourseDetailComponent - courseId:', courseId);
    
    if (courseId) {
      this.loadCourse(courseId);
    } else {
      console.log('No course ID found, redirecting to courses');
      this.router.navigate(['/courses']);
    }
  }

  loadCourse(id: string): void {
    console.log('Loading course with ID:', id);
    this.loading = true;
    this.error = false;
    
    this.courseService.getCourseById(id).subscribe({
      next: (course) => {
        console.log('Course loaded successfully:', course);
        
        if (!course) {
          console.error('Course data is null or undefined');
          this.loading = false;
          this.error = true;
          this.snackBar.open('Course data not found', 'Close', {
            duration: 5000,
            horizontalPosition: 'right',
            verticalPosition: 'top'
          });
          return;
        }
        
        this.course = course;
        this.loading = false;
        this.error = false;
      },
      error: (error) => {
        console.error('Error loading course:', error);
        this.loading = false;
        this.error = true;
        this.snackBar.open('Error loading course. Please try again.', 'Close', {
          duration: 5000,
          horizontalPosition: 'right',
          verticalPosition: 'top'
        });
      }
    });
  }

  editCourse(): void {
    if (this.course?.id) {
      console.log('Navigating to edit course:', this.course.id);
      this.router.navigate(['/courses/edit', this.course.id]);
    }
  }

  deleteCourse(): void {
    if (!this.course?.id) return;

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Delete Course',
        message: 'Are you sure you want to delete this course? This action cannot be undone.',
        confirmText: 'Delete',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('Deleting course:', this.course!.id);
        this.courseService.deleteCourse(this.course!.id!).subscribe({
          next: () => {
            console.log('Course deleted successfully');
            this.snackBar.open('Course deleted successfully', 'Close', {
              duration: 3000,
              horizontalPosition: 'right',
              verticalPosition: 'top'
            });
            this.router.navigate(['/courses']);
          },
          error: (error) => {
            console.error('Error deleting course:', error);
            this.snackBar.open('Error deleting course. Please try again.', 'Close', {
              duration: 5000,
              horizontalPosition: 'right',
              verticalPosition: 'top'
            });
          }
        });
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/courses']);
  }
}