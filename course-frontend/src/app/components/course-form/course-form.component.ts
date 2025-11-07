import { Component, OnInit, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CourseService } from '../../services/course.service';
import { UnitService } from '../../services/unit.service';
import { FilterOptionsService, FilterOptions } from '../../services/filter-options.service';
import { Course } from '../../models/course.model';
import { Unit } from '../../models/unit.model';

@Component({
  selector: 'app-course-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatChipsModule,
    MatIconModule,
    MatSnackBarModule,
    MatDialogModule,
    MatProgressSpinnerModule
    ,ConfirmDialogComponent
  ],
  templateUrl: './course-form.component.html',
  styleUrls: ['./course-form.component.scss']
})
export class CourseFormComponent implements OnInit {
  courseForm: FormGroup;
  isSubmitting = false;
  isEditMode = false;
  courseId: string | null = null;
  availableUnits: Unit[] = [];
  selectedUnits: string[] = [];
  loading = true;

  // Form options - will be loaded from backend
  boardOptions: string[] = [];
  mediumOptions: string[] = [];
  gradeOptions: string[] = [];
  subjectOptions: string[] = [];
  filterOptionsLoading: boolean = true;

  constructor(
    private fb: FormBuilder,
    private courseService: CourseService,
    private unitService: UnitService,
    private filterOptionsService: FilterOptionsService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {
    this.courseForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3),Validators.maxLength(30)]],
      description: ['', [Validators.required, Validators.minLength(10),Validators.maxLength(100)]],
      board: ['', Validators.required],
      medium: [[], [Validators.required, Validators.minLength(1),Validators.maxLength(3)]],
      grade: [[], [Validators.required, Validators.minLength(1),Validators.maxLength(5)]],
      subject: [[], [Validators.required, Validators.minLength(1),Validators.maxLength(3)]],
      units: [[]]
    });
  }
  private forceRevalidateForm(): void {
  Object.keys(this.courseForm.controls).forEach(key => {
    const control = this.courseForm.get(key);

    if (control) {
      control.setErrors(null);   // ✅ clear old errors
      control.updateValueAndValidity({ onlySelf: true, emitEvent: false }); // ✅ recalc errors
    }
  });

  this.courseForm.updateValueAndValidity({ emitEvent: false });
}


  // ngOnInit(): void {
  //   console.log('CourseFormComponent ngOnInit called');
    
  //   // Load filter options first
  //   this.loadFilterOptions();
    
  //   // Get course ID from route
  //   this.courseId = this.route.snapshot.paramMap.get('id');
  //   this.isEditMode = !!this.courseId;
    
  //   console.log('Course ID:', this.courseId);
  //   console.log('Is Edit Mode:', this.isEditMode);
    
  //   if (this.isEditMode && this.courseId) {
  //     this.loadCourseForEdit();
  //   } else {
  //     this.loading = false;
  //     this.loadAvailableUnits();
  //   }
  // }
ngOnInit(): void {  
  this.loadFilterOptions();

  // Determine mode first
  this.courseId = this.route.snapshot.paramMap.get('id');
  this.isEditMode = !!this.courseId;

  // ✅ Apply stepwise disabling ONLY for Add Course
  if (!this.isEditMode) {
    this.setupStepwiseFormValidation();
  }

  if (this.isEditMode && this.courseId) {
    this.loadCourseForEdit();
  } else {
    this.loading = false;
    this.loadAvailableUnits();
  }
}

setupStepwiseFormValidation(): void {

  // Step 1 → Enable Description only if Name is valid
  this.courseForm.get('name')?.statusChanges.subscribe(status => {
    if (status === 'VALID') {
      this.courseForm.get('description')?.enable();
    } else {
      this.courseForm.get('description')?.disable();
    }
  });

  // Step 2 → Enable Board only if Description valid
  this.courseForm.get('description')?.statusChanges.subscribe(status => {
    if (status === 'VALID') {
      this.courseForm.get('board')?.enable();
    } else {
      this.courseForm.get('board')?.disable();
    }
  });

  // Step 3 → Medium enabled when Board valid
  this.courseForm.get('board')?.statusChanges.subscribe(status => {
    if (status === 'VALID') {
      this.courseForm.get('medium')?.enable();
    } else {
      this.courseForm.get('medium')?.disable();
    }
  });

  // Step 4 → Grade enabled when Medium valid
  this.courseForm.get('medium')?.statusChanges.subscribe(status => {
    if (status === 'VALID') {
      this.courseForm.get('grade')?.enable();
    } else {
      this.courseForm.get('grade')?.disable();
    }
  });

  // Step 5 → Subject enabled when Grade valid
  this.courseForm.get('grade')?.statusChanges.subscribe(status => {
    if (status === 'VALID') {
      this.courseForm.get('subject')?.enable();
    } else {
      this.courseForm.get('subject')?.disable();
    }
  });

  // Step 6 → Units enabled when Subject valid
  this.courseForm.get('subject')?.statusChanges.subscribe(status => {
    if (status === 'VALID') {
      this.courseForm.get('units')?.enable();
    } else {
      this.courseForm.get('units')?.disable();
    }
  });

  // disable all except first (name)
  this.courseForm.get('description')?.disable();
  this.courseForm.get('board')?.disable();
  this.courseForm.get('medium')?.disable();
  this.courseForm.get('grade')?.disable();
  this.courseForm.get('subject')?.disable();
  this.courseForm.get('units')?.disable();
}

  loadFilterOptions(): void {
    this.filterOptionsLoading = true;
    this.filterOptionsService.getFilterOptions().subscribe({
      next: (options: FilterOptions) => {
        this.boardOptions = options.boards;
        this.mediumOptions = options.mediums;
        this.gradeOptions = options.grades;
        this.subjectOptions = options.subjects;
        this.filterOptionsLoading = false;
        console.log('Filter options loaded in form:', options);
      },
      error: (error) => {
        console.error('Error loading filter options:', error);
        // Fallback to hardcoded values if API fails
        this.boardOptions = ['XYZ','State', 'CBSE', 'ICSE'];
        this.mediumOptions = ['English', 'Kannada', 'Hindi', 'Telugu'];
        this.gradeOptions = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'];
        this.subjectOptions = [
          'English', 'Kannada', 'Hindi', 'Maths', 'Science', 'Social', 
          'Physics', 'Chemistry', 'Biology', 'History', 'Geography', 'Civics', 'Computer'
        ];
        this.filterOptionsLoading = false;
        this.snackBar.open('Using fallback filter options', 'Close', {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top'
        });
      }
    });
  }

  loadCourseForEdit(): void {
    if (!this.courseId) {
      // console.log('No course ID provided');
      this.loading = false;
      return;
    }
    
    console.log('Loading course for edit with ID:', this.courseId);
    
    this.courseService.getCourseById(this.courseId).subscribe({
      next: (course) => {
        console.log('Course loaded successfully:', course);
        
        if (!course) {
          console.error('Course data is null or undefined');
          this.snackBar.open('Course data not found', 'Close', {
            duration: 5000,
            horizontalPosition: 'right',
            verticalPosition: 'top'
          });
          this.loading = false;
          return;
        }
        
        // Patch form with course data
        this.courseForm.patchValue({
          name: course.name || '',
          description: course.description || '',
          board: course.board || '',
          medium: course.medium || [],
          grade: course.grade || [],
          subject: course.subject || [],
          units: course.units || []
        });
        // ✅ Edit mode: ensure ALL controls are enabled
      Object.keys(this.courseForm.controls).forEach(key => {
        this.courseForm.get(key)?.enable({ emitEvent: false });
      });

      this.forceRevalidateForm();

      // ✅ Force revalidation (important)
      Object.keys(this.courseForm.controls).forEach(key => {
        this.courseForm.get(key)?.updateValueAndValidity({ onlySelf: true, emitEvent: false });
      });
      this.courseForm.updateValueAndValidity({ onlySelf: false, emitEvent: false });
        
        // Set selected units
        this.selectedUnits = course.units?.map(unit => unit.id).filter(id => id !== undefined) as string[] || [];
        
        console.log('Form patched with values:', this.courseForm.value);
        this.loading = false;
        
        // Load available units after course is loaded
        this.loadAvailableUnits();
      },
      error: (error) => {
        console.error('Error loading course for edit:', error);
        this.snackBar.open('Error loading course for editing. Please try again.', 'Close', {
          duration: 5000,
          horizontalPosition: 'right',
          verticalPosition: 'top'
        });
        this.loading = false;
      }
    });
  }

  loadAvailableUnits(): void {
    console.log('Loading available units...');
    if (!this.courseId) {
      // In create mode, do not show global units; units are scoped per course
      this.availableUnits = [];
      return;
    }

    this.unitService.getUnitsByCourse(this.courseId).subscribe({
      next: (units) => {
        console.log('Units loaded successfully for course:', this.courseId, units);
        this.availableUnits = units || [];
      },
      error: (error) => {
        console.error('Error loading units:', error);
        this.availableUnits = [];
        this.snackBar.open('Error loading units', 'Close', {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top'
        });
      }
    });
  }

  onSubmit(): void {
    if (this.courseForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;
      const formValue = this.courseForm.value;
      
      // Build units array from selectedUnits
      const unitsForCourse = this.selectedUnits.map(unitId => {
        const unit = this.availableUnits.find(u => u.id === unitId);
        return {
          id: unitId,
          title: unit?.title || '',
          content: unit?.content || ''
        };
      });
      
      const courseData: Course = {
        name: formValue.name,
        description: formValue.description,
        board: formValue.board,
        medium: formValue.medium,
        grade: formValue.grade,
        subject: formValue.subject,
        units: unitsForCourse
      };

      console.log('Submitting course data with units:', courseData);

      if (this.isEditMode && this.courseId) {
        this.updateCourse(courseData);
      } else {
        this.createCourse(courseData);
      }
    } else {
      console.log('Form is invalid:', this.courseForm.errors);
      this.snackBar.open('Please fill in all required fields correctly', 'Close', {
        duration: 3000,
        horizontalPosition: 'right',
        verticalPosition: 'top'
      });
    }
  }

  createCourse(courseData: Course): void {
    this.courseService.createCourse(courseData).subscribe({
      next: (createdCourse) => {
        console.log('Course created successfully:', createdCourse);
        this.snackBar.open('Course created successfully', 'Close', {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top'
        });
        this.router.navigate(['/courses']);
      },
      error: (error) => {
        console.error('Error creating course:', error);
        this.snackBar.open('Error creating course. Please try again.', 'Close', {
          duration: 5000,
          horizontalPosition: 'right',
          verticalPosition: 'top'
        });
        this.isSubmitting = false;
      }
    });
  }

  updateCourse(courseData: Course): void {
    if (!this.courseId) return;
    
    this.courseService.updateCourse(this.courseId, courseData).subscribe({
      next: (updatedCourse) => {
        console.log('Course updated successfully:', updatedCourse);
        this.snackBar.open('Course updated successfully', 'Close', {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top'
        });
        this.router.navigate(['/courses']);
      },
      error: (error) => {
        console.error('Error updating course:', error);
        this.snackBar.open('Error updating course. Please try again.', 'Close', {
          duration: 5000,
          horizontalPosition: 'right',
          verticalPosition: 'top'
        });
        this.isSubmitting = false;
      }
    });
  }

  associateUnitsWithCourse(courseId: string, unitIds: string[]): void {
    let completedCount = 0;
    const totalUnits = unitIds.length;

    unitIds.forEach(unitId => {
      // Get the unit first, then update it with the courseId
      this.unitService.getUnitById(unitId).subscribe({
        next: (unit) => {
          const updatedUnit = { ...unit, courseId: courseId };
          
          this.unitService.updateUnit(unitId, updatedUnit).subscribe({
            next: () => {
              completedCount++;
              console.log(`Unit ${unitId} associated with course ${courseId}`);
              
              if (completedCount === totalUnits) {
                this.snackBar.open('Course and units saved successfully', 'Close', {
                  duration: 3000,
                  horizontalPosition: 'right',
                  verticalPosition: 'top'
                });
                this.router.navigate(['/courses']);
              }
            },
            error: (error) => {
              console.error('Error associating unit:', error);
              completedCount++;
              
              if (completedCount === totalUnits) {
                this.snackBar.open('Course saved, but some units could not be associated', 'Close', {
                  duration: 5000,
                  horizontalPosition: 'right',
                  verticalPosition: 'top'
                });
                this.router.navigate(['/courses']);
              }
            }
          });
        },
        error: (error) => {
          console.error('Error fetching unit:', error);
          completedCount++;
          
          if (completedCount === totalUnits) {
            this.snackBar.open('Course saved, but some units could not be associated', 'Close', {
              duration: 5000,
              horizontalPosition: 'right',
              verticalPosition: 'top'
            });
            this.router.navigate(['/courses']);
          }
        }
      });
    });
  }

  onCancel(): void {
    this.router.navigate(['/courses']);
  }

  onUnitSelectionChange(unitIds: string[]): void {
    this.selectedUnits = unitIds;
    console.log('Selected units changed:', this.selectedUnits);
  }

 openAddUnitDialog(): void {
  const dialogRef = this.dialog.open(AddUnitDialogComponent, {
    width: '500px',
    data: { courseId: this.courseId } // null is OK for add mode
  });

  dialogRef.afterClosed().subscribe(result => {
    if (!result) return;

    const map = new Map<string, Unit>();
    [...this.availableUnits, result as Unit].forEach(u => {
      if (u && u.id) map.set(u.id, u);
    });

    this.availableUnits = Array.from(map.values());

    const newId = String(result.id);
    if (!this.selectedUnits.includes(newId)) {
      this.selectedUnits = [...this.selectedUnits, newId];
    }
  });
}


  // Open confirm dialog and remove unit on confirm
  confirmRemoveUnit(unitId: string): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Remove Unit',
        message: `Are you sure you want to remove "${this.getUnitTitle(unitId)}" from this course? This will not delete the unit from the database.`,
        confirmText: 'Remove',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.removeUnit(unitId);
      }
    });
  }

  // Button handler to prevent overlapping clicks and open dialog
  onDeleteUnitClick(event: Event, unitId: string): void {
    event.preventDefault();
    event.stopPropagation();
    this.confirmRemoveUnit(unitId);
  }

  get pageTitle(): string {
    return this.isEditMode ? 'Edit Course' : 'Add Course';
  }

  get submitButtonText(): string {
    return this.isEditMode ? 'Update Course' : 'Create Course';
  }

  getUnitTitle(unitId: string): string {
    const unit = this.availableUnits.find(u => u.id === unitId);
    return unit?.title || 'Unit';
  }

  removeUnit(unitId: string): void {
    this.selectedUnits = this.selectedUnits.filter(id => id !== unitId);
  }
}

// Add Unit Dialog Component
@Component({
  selector: 'app-add-unit-dialog',
  template: `
    <h2 mat-dialog-title>Add New Unit</h2>
    <mat-dialog-content>
      <form [formGroup]="unitForm" (ngSubmit)="onSubmit()">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Unit Title</mat-label>
          <input matInput formControlName="title" placeholder="Enter unit title">
          <mat-error *ngIf="unitForm.get('title')?.hasError('required')">
            Title is required
          </mat-error>
          <mat-error *ngIf="unitForm.get('title')?.hasError('minlength')">
            Title must be at least 3 characters
          </mat-error>
        </mat-form-field>
        
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Unit Content</mat-label>
          <textarea matInput formControlName="content" rows="4" placeholder="Enter unit content"></textarea>
          <mat-error *ngIf="unitForm.get('content')?.hasError('required')">
            Content is required
          </mat-error>
          <mat-error *ngIf="unitForm.get('content')?.hasError('minlength')">
            Content must be at least 10 characters
          </mat-error>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button [mat-dialog-close]="false">Cancel</button>
      <button mat-button [disabled]="!unitForm.valid || isSubmitting" (click)="onSubmit()" color="primary">
        <mat-icon>add</mat-icon>
        {{ isSubmitting ? 'Adding...' : 'Add Unit' }}
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    .full-width { width: 100%; margin-bottom: 16px; }
  `],
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatIconModule, ReactiveFormsModule]
})
export class AddUnitDialogComponent {
  unitForm: FormGroup;
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private unitService: UnitService,
    private snackBar: MatSnackBar,
    private dialogRef: MatDialogRef<AddUnitDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.unitForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      content: ['', [Validators.required, Validators.minLength(5)]]
    });
  }

  onSubmit(): void {
    if (this.unitForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;
      const unitData: Unit = {
        title: this.unitForm.value.title,
        content: this.unitForm.value.content,
        courseId: this.data.courseId
      };

      console.log('Creating unit:', unitData);

      this.unitService.createUnit(unitData).subscribe({
        next: (createdUnit) => {
          console.log('Unit created successfully:', createdUnit);
          this.snackBar.open('Unit created successfully', 'Close', {
            duration: 3000,
            horizontalPosition: 'right',
            verticalPosition: 'top'
          });
          this.dialogRef.close(createdUnit);
        },
        error: (error) => {
          console.error('Error creating unit:', error);
          this.snackBar.open('Error creating unit. Please try again.', 'Close', {
            duration: 5000,
            horizontalPosition: 'right',
            verticalPosition: 'top'
          });
          this.isSubmitting = false;
        }
      });
    }
  }
}