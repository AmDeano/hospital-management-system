/**
 * Patient model for patient service communication
 */
export interface Patient {
  id: string;
  nom: string;
  dateNaissance: string; // ISO date format
  email: string;
  numeroTelephone?: string;
  adresse?: string;
  numeroSecuriteSociale?: string;
  cin?: string;
  isMinor: boolean;
  parentCin?: string;
  createdAt?: string;
  updatedAt?: string;
}

/**
 * DTO for patient creation/update
 */
export interface PatientDto extends Patient {}

/**
 * Search filters for patient search
 */
export interface PatientSearchFilters {
  name?: string;
  email?: string;
  cin?: string;
  isMinor?: boolean;
}
