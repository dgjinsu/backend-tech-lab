import client from './client';
import type { ApiResponse } from '../types/common';
import type { AuthResponse, LoginRequest, SignupRequest, User } from '../types/auth';

export const login = (data: LoginRequest) =>
  client.post<ApiResponse<AuthResponse>>('/auth/login', data);

export const signup = (data: SignupRequest) =>
  client.post<ApiResponse<void>>('/auth/signup', data);

export const refreshToken = (token: string) =>
  client.post<ApiResponse<AuthResponse>>('/auth/refresh', { refreshToken: token });

export const getMe = () =>
  client.get<ApiResponse<User>>('/auth/me');
