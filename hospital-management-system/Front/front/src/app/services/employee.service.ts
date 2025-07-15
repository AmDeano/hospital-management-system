import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export enum EmployeeType {
  ADMINISTRATION = 'ADMINISTRATION',
  MEDICAL_STAFF = 'MEDICAL_STAFF'
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
  licenceNumber?: string; // Professional license
  isActive?: boolean;
  supervisorMatricule?: string;
  workDays?: WorkDay[];
  shiftStart?: string; // e.g., "08:00"
  shiftEnd?: string; // e.g., "17:00"
  createdAt?: string;
  updatedAt?: string;
}

export interface EmployeeSearchCriteria {
  nom?: string;
  prenom?: string;
  departement?: string;
  employeeType?: EmployeeType;
  isActive?: boolean;
}

export interface EmployeeStatistics {
  departement: string;
  count: number;
  employeeType: EmployeeType;
}

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {
  private apiUrl = 'http://localhost:8082/api/employees';

  constructor(private http: HttpClient) {}

  // Basic CRUD operations
  getAllEmployees(): Observable<Employee[]> {
    return this.http.get<Employee[]>(this.apiUrl);
  }

  getEmployeeByMatricule(matricule: string): Observable<Employee> {
    return this.http.get<Employee>(`${this.apiUrl}/${matricule}`);
  }

  createEmployee(employee: Employee): Observable<Employee> {
    return this.http.post<Employee>(this.apiUrl, employee);
  }

  updateEmployee(matricule: string, employee: Employee): Observable<Employee> {
    return this.http.put<Employee>(`${this.apiUrl}/${matricule}`, employee);
  }

  deleteEmployee(matricule: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${matricule}`);
  }

  // Search and filter operations
  findByEmail(email: string): Observable<Employee> {
    return this.http.get<Employee>(`${this.apiUrl}/email/${email}`);
  }

  findByCin(cin: string): Observable<Employee> {
    return this.http.get<Employee>(`${this.apiUrl}/cin/${cin}`);
  }

  findByLicenceNumber(licenceNumber: string): Observable<Employee> {
    return this.http.get<Employee>(`${this.apiUrl}/licence/${licenceNumber}`);
  }

  findByEmployeeType(employeeType: EmployeeType): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.apiUrl}/type/${employeeType}`);
  }

  findActiveEmployees(): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.apiUrl}/active`);
  }

  findInactiveEmployees(): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.apiUrl}/inactive`);
  }

  findByDepartement(departement: string): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.apiUrl}/department/${departement}`);
  }

  findByDepartementAndType(departement: string, employeeType: EmployeeType): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.apiUrl}/department/${departement}/type/${employeeType}`);
  }

  findBySpecialite(specialite: string): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.apiUrl}/speciality/${specialite}`);
  }

  findBySupervisor(supervisorMatricule: string): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.apiUrl}/supervisor/${supervisorMatricule}`);
  }

  searchByName(nom?: string, prenom?: string): Observable<Employee[]> {
    let params = new HttpParams();
    if (nom) params = params.set('nom', nom);
    if (prenom) params = params.set('prenom', prenom);
    return this.http.get<Employee[]>(`${this.apiUrl}/search/name`, { params });
  }

  searchByFullName(fullName: string): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.apiUrl}/search/fullname/${fullName}`);
  }

  findByDateRange(startDate: string, endDate: string): Observable<Employee[]> {
    let params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get<Employee[]>(`${this.apiUrl}/hired-between`, { params });
  }

  findByWorkDays(workDays: WorkDay[]): Observable<Employee[]> {
    let params = new HttpParams();
    workDays.forEach(day => params = params.append('workDays', day));
    return this.http.get<Employee[]>(`${this.apiUrl}/work-days`, { params });
  }

  findByShiftTime(shiftStart: string, shiftEnd: string): Observable<Employee[]> {
    let params = new HttpParams()
      .set('shiftStart', shiftStart)
      .set('shiftEnd', shiftEnd);
    return this.http.get<Employee[]>(`${this.apiUrl}/shift`, { params });
  }

  findActiveMedicalStaffBySpecialityAndDepartment(specialite: string, departement: string): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.apiUrl}/medical-staff/speciality/${specialite}/department/${departement}`);
  }

  findAvailableDoctorsByWorkDay(workDay: WorkDay): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.apiUrl}/doctors/available/${workDay}`);
  }

  searchEmployees(criteria: EmployeeSearchCriteria): Observable<Employee[]> {
    let params = new HttpParams();
    if (criteria.nom) params = params.set('nom', criteria.nom);
    if (criteria.prenom) params = params.set('prenom', criteria.prenom);
    if (criteria.departement) params = params.set('departement', criteria.departement);
    if (criteria.employeeType) params = params.set('employeeType', criteria.employeeType);
    if (criteria.isActive !== undefined) params = params.set('isActive', criteria.isActive.toString());
    return this.http.get<Employee[]>(`${this.apiUrl}/search`, { params });
  }

  // Statistics and counts
  countByEmployeeType(employeeType: EmployeeType): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count/type/${employeeType}`);
  }

  countByDepartement(departement: string): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count/department/${departement}`);
  }

  getEmployeeStatisticsByDepartment(): Observable<EmployeeStatistics[]> {
    return this.http.get<EmployeeStatistics[]>(`${this.apiUrl}/statistics/department`);
  }

  // Validation methods
  checkEmailExists(email: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/exists/email/${email}`);
  }

  checkCinExists(cin: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/exists/cin/${cin}`);
  }

  checkLicenceNumberExists(licenceNumber: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/exists/licence/${licenceNumber}`);
  }

  // Special queries
  findMedicalStaffWithLicence(): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.apiUrl}/medical-staff/with-licence`);
  }

  findEmployeesWithoutSupervisor(): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.apiUrl}/no-supervisor`);
  }

  findByTelephone(telephone: string): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.apiUrl}/telephone/${telephone}`);
  }

  // Utility methods
  checkHealth(): Observable<string> {
    return this.http.get(`${this.apiUrl}/health`, { responseType: 'text' });
  }

  // Helper methods for UI
  getEmployeeTypeDisplayName(type: EmployeeType): string {
    switch (type) {
      case EmployeeType.ADMINISTRATION:
        return 'Administration';
      case EmployeeType.MEDICAL_STAFF:
        return 'Medical Staff';
      default:
        return type;
    }
  }

  getWorkDayDisplayName(day: WorkDay): string {
    return day.charAt(0) + day.slice(1).toLowerCase();
  }
}