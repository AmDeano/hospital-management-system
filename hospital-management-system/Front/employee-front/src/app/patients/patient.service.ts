import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Patient, PatientDto, PatientSearchFilters } from './patient.model';
import { ConfigService } from '../core/config.service';

/**
 * Service for communicating with the patient-service backend
 */
@Injectable({
  providedIn: 'root'
})
export class PatientService {

  private API_URL: string;
  private MINORS_URL: string;

  constructor(
    private http: HttpClient,
    private config: ConfigService
  ) {
    const baseUrl = this.config.getPatientServiceUrl();
    this.API_URL = `${baseUrl}/patients`;
    this.MINORS_URL = `${baseUrl}/minors`;
  }

  /**
   * Get all patients
   */
  getAllPatients(): Observable<Patient[]> {
    return this.http.get<Patient[]>(this.API_URL);
  }

  /**
   * Get patient by ID
   */
  getPatientById(id: string): Observable<Patient> {
    return this.http.get<Patient>(`${this.API_URL}/${id}`);
  }

  /**
   * Get patient by CIN
   */
  getPatientByCin(cin: string): Observable<Patient> {
    return this.http.get<Patient>(`${this.API_URL}/cin/${cin}`);
  }

  /**
   * Get patient by email
   */
  getPatientByEmail(email: string): Observable<Patient> {
    return this.http.get<Patient>(`${this.API_URL}/email/${email}`);
  }

  /**
   * Search patients by name (case-insensitive)
   */
  searchPatientsByName(name: string): Observable<Patient[]> {
    const params = new HttpParams().set('name', name);
    return this.http.get<Patient[]>(this.API_URL, { params });
  }

  /**
   * Update patient
   */
  updatePatient(id: string, patient: PatientDto): Observable<Patient> {
    return this.http.put<Patient>(`${this.API_URL}/${id}`, patient);
  }

  /**
   * Delete patient
   */
  deletePatient(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  /**
   * Get all minors
   */
  getAllMinors(): Observable<Patient[]> {
    return this.http.get<Patient[]>(this.MINORS_URL);
  }

  /**
   * Get minors by parent CIN
   */
  getMinorsByParentCin(parentCin: string): Observable<Patient[]> {
    return this.http.get<Patient[]>(`${this.MINORS_URL}/parent/${parentCin}`);
  }

  /**
   * Advanced search with multiple filters
   */
  searchPatients(filters: PatientSearchFilters): Observable<Patient[]> {
    let params = new HttpParams();

    if (filters.name) {
      params = params.set('name', filters.name);
    }
    if (filters.email) {
      params = params.set('email', filters.email);
    }
    if (filters.cin) {
      params = params.set('cin', filters.cin);
    }
    if (filters.isMinor !== undefined) {
      params = params.set('isMinor', filters.isMinor.toString());
    }

    return this.http.get<Patient[]>(this.API_URL, { params });
  }
}
