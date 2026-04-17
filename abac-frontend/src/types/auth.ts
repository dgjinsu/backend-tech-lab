export type Role = 'EMPLOYEE' | 'MANAGER' | 'FINANCE' | 'ADMIN';

export interface AuthUser {
  userId: number;
  username: string;
  role: Role;
  departmentId: number;
}

export interface LoginResponse {
  token: string;
  username: string;
  role: Role;
  departmentId: number;
}
