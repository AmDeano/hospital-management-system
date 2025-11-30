import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Nurse, NurseDto, NurseSearchFilters } from './nurse.model';
import { ConfigService } from '../core/config.service';

/**
 * Service for communicating with the employee-service backend (nurses endpoint)
 */
@Injectable({
  providedIn: 'root'
})
export class NurseService {

  private NURSES_URL: string;

  constructor(
    private http: HttpClient,
    private config: ConfigService
  ) {
    this.NURSES_URL = `${this.config.getEmployeeServiceUrl()}/nurses`;
  }

  /**
   * Get all nurses
   */
  getAllNurses(): Observable<Nurse[]> {
    return this.http.get<Nurse[]>(this.NURSES_URL);
  }

  /**
   * Get nurse by ID
   */
  getNurseById(id: number): Observable<Nurse> {
    return this.http.get<Nurse>(`${this.NURSES_URL}/${id}`);
  }

  /**
   * Get nurses by shift
   */
  getNursesByShift(shift: string): Observable<Nurse[]> {
    return this.http.get<Nurse[]>(`${this.NURSES_URL}/shift/${shift}`);
  }

  /**
   * Get active nurses by shift
   */
  getActiveNursesByShift(shift: string): Observable<Nurse[]> {
    return this.http.get<Nurse[]>(`${this.NURSES_URL}/shift/${shift}/active`);
  }

  /**
   * Create new nurse
   */
  createNurse(nurse: NurseDto): Observable<Nurse> {
    return this.http.post<Nurse>(this.NURSES_URL, nurse);
  }

  /**
   * Update nurse
   */
  updateNurse(id: number, nurse: NurseDto): Observable<Nurse> {
    return this.http.put<Nurse>(`${this.NURSES_URL}/${id}`, nurse);
  }

  /**
   * Delete nurse
   */
  deleteNurse(id: number): Observable<void> {
    return this.http.delete<void>(`${this.NURSES_URL}/${id}`);
  }

  /**
   * Advanced search with filters
   */
  searchNurses(filters: NurseSearchFilters): Observable<Nurse[]> {
    let params = new HttpParams();

    if (filters.shift) {
      params = params.set('shift', filters.shift);
    }
    if (filters.isActive !== undefined) {
      params = params.set('isActive', filters.isActive.toString());
    }
    if (filters.email) {
      params = params.set('email', filters.email);
    }

    return this.http.get<Nurse[]>(this.NURSES_URL, { params });
  }
}
