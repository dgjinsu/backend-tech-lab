export interface Category {
  id: number;
  name: string;
  type: 'DEFAULT' | 'CUSTOM';
}

export interface CreateCategoryRequest {
  name: string;
}
