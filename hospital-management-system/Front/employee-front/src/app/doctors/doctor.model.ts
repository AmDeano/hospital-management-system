/**
 * Doctor model
 */
export interface Doctor {
  id: number;
  matricule: string;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  address?: string;
  specialization: string;
  licenseNumber: string;
  medicalDegree?: string;
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
 * DTO for doctor creation/update
 */
export interface DoctorDto extends Omit<Doctor, 'id' | 'createdAt' | 'updatedAt'> {}

/**
 * Search filters for doctor search
 */
export interface DoctorSearchFilters {
  specialization?: string;
  isActive?: boolean;
  department?: string;
  email?: string;
}

/**
 * Doctor Dashboard model with all functionalities
 */
export interface DoctorDashboard {
  id: number;
  matricule: string;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  specialization: string;
  licenseNumber: string;
  medicalDegree?: string;
  departmentName: string;
  isActive: boolean;

  // Dashboard stats
  totalPatientsCount: number;
  appointmentsTodayCount: number;
  pendingPrescriptionsCount: number;
  recentMedicalRecordsCount: number;

  // Doctor's schedules and availability
  schedules: DoctorSchedule[];
  availabilitySlots: DoctorAvailabilitySlot[];
}

/**
 * Doctor Schedule model
 */
export interface DoctorSchedule {
  id: number;
  doctorId: number;
  dayOfWeek: string;
  startTime: string;
  endTime: string;
  isAvailable: boolean;
}

/**
 * Doctor Availability Slot model
 */
export interface DoctorAvailabilitySlot {
  id: number;
  doctorId: number;
  availableDate: string;
  startTime: string;
  endTime: string;
  isAvailable: boolean;
  notes?: string;
}

/**
 * Prescription model
 */
export interface Prescription {
  id: number;
  patientId: string;
  doctorId: number;
  medicationName: string;
  dosage: string;
  frequency: string;
  durationDays: number;
  notes?: string;
  isActive: boolean;
  createdAt: string;
}

/**
 * Medical Record model
 */
export interface MedicalRecord {
  id: number;
  patientId: string;
  doctorId: number;
  diagnosis: string;
  treatmentPlan?: string;
  notes?: string;
  recordType: 'CONSULTATION' | 'DIAGNOSIS' | 'TREATMENT' | 'FOLLOW_UP';
  visitDate: string;
  createdAt: string;
}

/**
 * Diagnostic Report model
 */
export interface DiagnosticReport {
  id: number;
  patientId: string;
  doctorId: number;
  testId: number;
  resultValue: string;
  reportDate: string;
  status: 'PENDING' | 'COMPLETED' | 'ABNORMAL';
  notes?: string;
  attachmentUrl?: string;
  createdAt: string;
}

/**
 * Test Catalog model
 */
export interface TestCatalog {
  id: number;
  testName: string;
  description?: string;
  category: string;
  normalRange: string;
  unit?: string;
  isActive: boolean;
}

/**
 * Consultation Fee model
 */
export interface ConsultationFee {
  id: number;
  doctorId: number;
  specialization?: string;
  feeAmount: number;
  currency: string;
  isActive: boolean;
}

/**
 * Patient Insurance model
 */
export interface PatientInsurance {
  id: number;
  patientId: string;
  insuranceProvider?: string;
  policyNumber?: string;
  coveragePercentage: number;
  isActive: boolean;
  expiryDate?: string;
}
