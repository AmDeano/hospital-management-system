// src/app/employees/employee.model.ts

export interface Employee {
  matricule: string;
  nom: string;
  prenom: string;
  poste: string;
  employeeType: 'ADMINISTRATION' | 'MEDICAL_STAFF';
  departement: string;
  telephone?: string;
  email?: string;
  dateEmbauche: string;
  dateNaissance?: string;
  adresse?: string;
  numeroSecuriteSociale?: string;
  cin?: string;
  specialite?: string;
  licenceNumber?: string;
  isActive?: boolean;
  supervisorMatricule?: string;
  workDays?: string[];
  shiftStart?: string;
  shiftEnd?: string;
  createdAt?: string;
  updatedAt?: string;
}
