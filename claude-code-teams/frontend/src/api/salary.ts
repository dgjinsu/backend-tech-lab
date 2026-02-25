import client from './client';
import type { ApiResponse } from '../types/common';
import type { Salary, CreateSalaryRequest, UpdateSalaryRequest } from '../types/salary';

export const getSalaries = (year: number) =>
  client.get<ApiResponse<Salary[]>>('/salaries', { params: { year } });

export const getSalary = (id: number) =>
  client.get<ApiResponse<Salary>>(`/salaries/${id}`);

export const createSalary = (data: CreateSalaryRequest) =>
  client.post<ApiResponse<Salary>>('/salaries', data);

export const updateSalary = (id: number, data: UpdateSalaryRequest) =>
  client.patch<ApiResponse<Salary>>(`/salaries/${id}`, data);

export const deleteSalary = (id: number) =>
  client.delete<ApiResponse<void>>(`/salaries/${id}`);
