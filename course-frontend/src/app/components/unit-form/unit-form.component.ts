import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';
import { UnitService } from '../../services/unit.service';
import { Unit } from '../../models/unit.model';

@Component({
  selector: 'app-unit-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './unit-form.component.html',
  styleUrls: ['./unit-form.component.scss']
})
export class UnitFormComponent implements OnInit {
  unitForm: FormGroup;
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private unitService: UnitService,
    private router: Router
  ) {
    this.unitForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      content: ['', [Validators.required, Validators.minLength(10)]]
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.unitForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;
      const formValue = this.unitForm.value;
      const unitData: Unit = {
        title: formValue.title,
        content: formValue.content,
        courseId: formValue.courseId || undefined
      };

      this.unitService.createUnit(unitData).subscribe({
        next: (createdUnit) => {
          console.log('Unit created successfully:', createdUnit);
          this.router.navigate(['/courses']);
        },
        error: (error) => {
          console.error('Error creating unit:', error);
          this.isSubmitting = false;
        }
      });
    }
  }

  onBackToCourses(): void {
    this.router.navigate(['/courses']);
  }

  getErrorMessage(fieldName: string): string {
    const field = this.unitForm.get(fieldName);
    if (field?.hasError('required')) {
      return `${fieldName} is required`;
    }
    if (field?.hasError('minlength')) {
      return `${fieldName} must be at least ${field.errors?.['minlength'].requiredLength} characters`;
    }
    return '';
  }
}
