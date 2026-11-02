import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');
  const authReq = req.clone({
    withCredentials: true,
    ...(token ? { headers: req.headers.set('Authorization', `Bearer ${token}`) } : {})
  });

  return next(authReq);
};