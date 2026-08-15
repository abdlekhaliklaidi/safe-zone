import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService, RegisterRequest } from '../../services/auth.service';
import { MediaService } from '../../services/media.service';

export type UserRole = 'SELLER' | 'CLIENT';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    FormsModule
  ],
  templateUrl: './register.component.html'
})
export class RegisterComponent implements OnInit {

  private fb = inject(NonNullableFormBuilder);
  private authService = inject(AuthService);
  private mediaService = inject(MediaService);
  private router = inject(Router);

  avatarPreview: string | null = null;
  selectedAvatar: File | null = null;
  loading = false;
  error = '';

  form = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    role: ['SELLER' as UserRole, [Validators.required, Validators.pattern(/^(SELLER|CLIENT)$/)]]
  });

  ngOnInit(): void {
    // Automatically sweeps away the global error banner whenever the user updates any input
    this.form.valueChanges.subscribe(() => {
      if (this.error) {
        this.error = '';
      }
    });
  }

  onAvatarSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files?.length) return;

    const file = input.files[0];
    this.selectedAvatar = file;

    const reader = new FileReader();
    reader.onload = () => {
      this.avatarPreview = reader.result as string;
    };
    reader.readAsDataURL(file);
  }

  register(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.error = '';

    if (this.selectedAvatar) {
      // Call dedicated public avatar endpoint
      this.mediaService.uploadPublicAvatar(this.selectedAvatar).subscribe({
        next: (res) => {
          this.registerUser(res.avatarUrl);
        },
        error: (er) => {
          console.error('Avatar upload failed:', er);
          this.loading = false;
          this.error = er?.error?.error ?? 'Failed to upload avatar.';
        }
      });
    } else {
      this.registerUser('');
    }
  }

  private registerUser(avatarUrl: string): void {
    const formValues = this.form.getRawValue();

    const data: RegisterRequest = {
      username: formValues.username,
      email: formValues.email,
      password: formValues.password,
      role: formValues.role,
      avatarUrl
    };

    this.authService.register(data).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('Backend registration error:', err);
        this.loading = false;
        this.error = err?.error?.errorMessage ?? 'Registration failed. Please try again later.';
      }
    });
  }
}