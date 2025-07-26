// src/app/employees/employee.model.ts

export enum EmployeeType {
  MEDICAL_STAFF = 'MEDICAL_STAFF',
  ADMINISTRATION = 'ADMINISTRATION'
}

export enum WorkDay {
  MONDAY = 'MONDAY',
  TUESDAY = 'TUESDAY',
  WEDNESDAY = 'WEDNESDAY',
  THURSDAY = 'THURSDAY',
  FRIDAY = 'FRIDAY',
  SATURDAY = 'SATURDAY',
  SUNDAY = 'SUNDAY'
}

export interface Employee {
  matricule: string;
  nom: string;
  prenom: string;
  poste: string;
  employeeType: EmployeeType;
  departement?: string;
  telephone?: string;
  email?: string;
  dateEmbauche: string; // ISO date string
  dateNaissance?: string; // ISO date string
  adresse?: string;
  numeroSecuriteSociale?: string;
  cin?: string;
  specialite?: string; // For medical staff
  licenceNumber?: string;
  isActive?: boolean;
  supervisorMatricule?: string;
  workDays?: WorkDay[];
  shiftStart?: string; // e.g., "08:00"
  shiftEnd?: string; // e.g., "17:00"
  createdAt?: string;
  updatedAt?: string;
}
/*
export interface SearchCriteria {
  matricule?: string;
  nom?: string;
  prenom?: string;
  poste?: string;
  employeeType?: EmployeeType;
  departement?: string;
  telephone?: string;
  email?: string;
  isActive?: boolean;
  [key: string]: any;
}
*/

export interface SearchCriteria {
  nom?: string;
  prenom?: string;
  departement?: string;
  employeeType?: string;   // or a union of your EmployeeType names
  isActive?: boolean;
}

export interface EmployeeCreateRequest {
  matricule: string;
  nom: string;
  prenom: string;
  poste: string;
  employeeType: EmployeeType;
  departement?: string;
  telephone?: string;
  email?: string;
  dateEmbauche: string;
  dateNaissance?: string;
  adresse?: string;
  numeroSecuriteSociale?: string;
  cin?: string;
  specialite?: string;
  licenceNumber?: string;
  supervisorMatricule?: string;
  workDays?: WorkDay[];
  shiftStart?: string;
  shiftEnd?: string;
}