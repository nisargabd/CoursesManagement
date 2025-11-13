import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import {
  CommonModule
} from '@angular/common';
import {
  ReactiveFormsModule
} from '@angular/forms';
import {
  MatFormFieldModule
} from '@angular/material/form-field';
import {
  MatInputModule
} from '@angular/material/input';
import {
  MatSelectModule
} from '@angular/material/select';
import {
  MatIconModule
} from '@angular/material/icon';
import {
  MatCardModule
} from '@angular/material/card';
import {
  MatButtonModule
} from '@angular/material/button';

@Component({
  selector: 'app-register',
  standalone: true,
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatCardModule,
    MatButtonModule
  ]
})
export class RegisterComponent {

  registerForm = this.fb.group({
    countryCode: ['+91', Validators.required],

    username: [
      '',
      [Validators.required, Validators.minLength(3), Validators.maxLength(15)]
    ],

    email: [
      '',
      [
        Validators.required,
        Validators.email,
        Validators.maxLength(40),
        Validators.pattern(/^[^\s@]+@[^\s@]+\.[^\s@]+$/)
      ]
    ],

    phone: [
      '',
      [Validators.required, Validators.pattern(/^[0-9]{7,12}$/)]
    ],

    password: [
      '',
      [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(25),
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/)
      ]
    ],

    role: ['', Validators.required]
});
  hidePassword: boolean = true;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {}
togglePasswordVisibility() {
    this.hidePassword = !this.hidePassword;
  }
  goToLogin() {
    this.router.navigate(['/login']);
  }

  onSubmit() {
    if (this.registerForm.invalid) return;

    const formValue = this.registerForm.value;
    const payload = {
      ...formValue,
      phone: `${formValue.countryCode}${formValue.phone}`,
      password: formValue.password?.trim()
    };

    this.auth.register(payload).subscribe({
      next: () => {
        alert('Registration successful!');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        if (err.error?.message?.includes('email already exists')) {
          alert('Email already registered. Try logging in.');
        } else {
          alert('Registration failed. Please try again.');
        }
      }
    });
    
  }
  
}
