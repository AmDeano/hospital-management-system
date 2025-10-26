/*
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { EmployeeService } from './employee.service';
import { Employee } from './employee.model';

describe('EmployeeService', () => {
  let service: EmployeeService;
  let httpMock: HttpTestingController;

  const mockEmployee: Employee = {
    matricule: 'EMP001',
    nom: 'Doe',
    prenom: 'John',
    cin: 'AB123456',
    email: 'john.doe@hospital.com',
    poste: 'Doctor',
    departement: 'Emergency',
    employeeType: 'MEDICAL_STAFF',
    dateEmbauche: '2023-01-15',
    workDays: ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'],
    isActive: true
  };

  const mockEmployees: Employee[] = [mockEmployee];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [EmployeeService]
    });
    service = TestBed.inject(EmployeeService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });
})
*/