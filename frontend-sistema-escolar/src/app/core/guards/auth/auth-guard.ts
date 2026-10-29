import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../../services/auth';
import { UserRole } from '../../../models/auth/user';

export const authGuard = (allowedRoles: UserRole[]): CanActivateFn => {
  return (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    const role = authService.getUserRole();
    if (authService.isAuthenticated() && role && allowedRoles.includes(role)) {
      return true;
    }

    router.navigate(['/login']);
    return false;
  };
};