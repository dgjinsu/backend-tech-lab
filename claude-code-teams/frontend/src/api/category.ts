import client from './client';
import type { ApiResponse } from '../types/common';
import type { Category, CreateCategoryRequest } from '../types/category';

export const getCategories = () =>
  client.get<ApiResponse<Category[]>>('/categories');

export const createCategory = (data: CreateCategoryRequest) =>
  client.post<ApiResponse<Category>>('/categories', data);

export const deleteCategory = (id: number) =>
  client.delete<ApiResponse<void>>(`/categories/${id}`);
