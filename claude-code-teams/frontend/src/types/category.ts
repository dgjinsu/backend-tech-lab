export interface Category {
  id: number;
  name: string;
  type: 'DEFAULT' | 'CUSTOM';
  color: string | null;
  icon: string | null;
}

export interface CreateCategoryRequest {
  name: string;
}
