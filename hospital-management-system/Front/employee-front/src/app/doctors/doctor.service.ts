import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Doctor, DoctorDto, DoctorSearchFilters, Department, DoctorDashboard,
  DoctorSchedule, DoctorAvailabilitySlot
} from './doctor.model';
import { ConfigService } from '../core/config.service';

/**
 * Service for communicating with the employee-service backend (doctors endpoint)
 */
@Injectable({
  providedIn: 'root'
})
export class DoctorService {

  private DOCTORS_URL: string;
  private DASHBOARD_URL: string;
  private DEPARTMENTS_URL: string;

  constructor(
    private http: HttpClient,
    private config: ConfigService
  ) {
    const baseUrl = this.config.getEmployeeServiceUrl();
    this.DOCTORS_URL = `${baseUrl}/doctors`;
    this.DASHBOARD_URL = `${baseUrl.replace('/employee-service/api', '')}/dashboard/doctors`;
    this.DEPARTMENTS_URL = `${baseUrl}/departments`;
  }

  /**
   * Get all doctors
   */
  getAllDoctors(): Observable<Doctor[]> {
    return this.http.get<Doctor[]>(this.DOCTORS_URL);
  }

  /**
   * Get doctor by ID
   */
  getDoctorById(id: number): Observable<Doctor> {
    return this.http.get<Doctor>(`${this.DOCTORS_URL}/${id}`);
  }

  /**
   * Get doctors by specialization
   */
  getDoctorsBySpecialization(specialization: string): Observable<Doctor[]> {
    return this.http.get<Doctor[]>(`${this.DOCTORS_URL}/specialization/${specialization}`);
  }

  /**
   * Get available doctors (active)
   */
  getAvailableDoctors(): Observable<Doctor[]> {
    return this.http.get<Doctor[]>(`${this.DOCTORS_URL}/available`);
  }

  /**
   * Create new doctor
   */
  createDoctor(doctor: DoctorDto): Observable<Doctor> {
    return this.http.post<Doctor>(this.DOCTORS_URL, doctor);
  }

  /**
   * Update doctor
   */
  updateDoctor(id: number, doctor: DoctorDto): Observable<Doctor> {
    return this.http.put<Doctor>(`${this.DOCTORS_URL}/${id}`, doctor);
  }

  /**
   * Delete doctor
   */
  deleteDoctor(id: number): Observable<void> {
    return this.http.delete<void>(`${this.DOCTORS_URL}/${id}`);
  }

  /**
   * Authorize patient discharge
   */
  authorizePatientDischarge(doctorId: number, patientId: string): Observable<string> {
    return this.http.post<string>(
      `${this.DOCTORS_URL}/${doctorId}/patients/${patientId}/discharge`,
      {}
    );
  }

  /**
   * Write medical certificate
   */
  writeMedicalCertificate(doctorId: number, patientId: string, details: string): Observable<string> {
    return this.http.post<string>(
      `${this.DOCTORS_URL}/${doctorId}/patients/${patientId}/certificate`,
      details
    );
  }

  /**
   * Get all departments
   */
  getDepartments(): Observable<Department[]> {
    return this.http.get<Department[]>(this.DEPARTMENTS_URL);
  }

  /**
   * Create department
   */
  createDepartment(department: Department): Observable<Department> {
    return this.http.post<Department>(this.DEPARTMENTS_URL, department);
  }

  /**
   * Advanced search with filters
   */
  searchDoctors(filters: DoctorSearchFilters): Observable<Doctor[]> {
    let params = new HttpParams();

    if (filters.specialization) {
      params = params.set('specialization', filters.specialization);
    }
    if (filters.isActive !== undefined) {
      params = params.set('isActive', filters.isActive.toString());
    }
    if (filters.email) {
      params = params.set('email', filters.email);
    }

    return this.http.get<Doctor[]>(this.DOCTORS_URL, { params });
  }

  // ========== Doctor Dashboard Methods ==========

  /**
   * Get doctor dashboard with all functionalities
   * GET /dashboard/doctors/{doctorMatricule}
   */
  getDoctorDashboard(doctorMatricule: string): Observable<DoctorDashboard> {
    return this.http.get<DoctorDashboard>(`${this.DASHBOARD_URL}/${doctorMatricule}`);
  }

  /**
   * Get doctor's personal information
   * GET /dashboard/doctors/{doctorMatricule}/info
   */
  getDoctorInfo(doctorMatricule: string): Observable<Doctor> {
    return this.http.get<Doctor>(`${this.DASHBOARD_URL}/${doctorMatricule}/info`);
  }

  /**
   * Get available appointment slots for a specific date
   * GET /dashboard/doctors/{doctorMatricule}/available-slots?date=yyyy-MM-dd
   */
  getAvailableSlots(doctorMatricule: string, date: string): Observable<DoctorAvailabilitySlot[]> {
    const params = new HttpParams().set('date', date);
    return this.http.get<DoctorAvailabilitySlot[]>(
      `${this.DASHBOARD_URL}/${doctorMatricule}/available-slots`,
      { params }
    );
  }
}
