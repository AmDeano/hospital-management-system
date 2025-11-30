/**
 * Nurse model
 */
export interface Nurse {
  id: number;
  matricule: string;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  address?: string;
  shift: string;
  nursingLicense: string;
  department?: Department;
  isActive: boolean;
  role?: string;
  createdAt?: string;
  updatedAt?: string;
}

/**
 * Department model
 */
export interface Department {
  id: number;
  name: string;
  description?: string;
}

/**
 * DTO for nurse creation/update
 */
export interface NurseDto extends Omit<Nurse, 'id' | 'createdAt' | 'updatedAt'> {}

/**
 * Search filters for nurse search
 */
export interface NurseSearchFilters {
  shift?: string;
  isActive?: boolean;
  email?: string;
}
