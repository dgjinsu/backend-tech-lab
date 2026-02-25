import client from './client';
import type { ApiResponse } from '../types/common';
import type { MonthlyStatistics, CategoryStatistics } from '../types/statistics';

export const getMonthlyStats = (year: number) =>
  client.get<ApiResponse<MonthlyStatistics[]>>('/statistics/monthly', { params: { year } });

export const getCategoryStats = (year: number, month: number) =>
  client.get<ApiResponse<CategoryStatistics[]>>('/statistics/category', { params: { year, month } });
