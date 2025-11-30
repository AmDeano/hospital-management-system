import { Injectable } from '@angular/core';

export interface ApiEndpoints {
  authService: string;
  patientService: string;
  employeeService: string;
}

/**
 * Configuration service that provides API endpoints based on environment
 * Supports both development (localhost) and Docker (service names) environments
 */
@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  private endpoints: ApiEndpoints;

  constructor() {
    this.endpoints = this.loadEndpoints();
  }

  /**
   * Detects the environment and loads appropriate API endpoints
   * In Docker: Uses service names (e.g., auth-service:8083)
   * In Development: Uses localhost with mapped ports
   */
  private loadEndpoints(): ApiEndpoints {
    const hostname = window.location.hostname;

    // Check for explicit Docker environment variable (set at build/runtime)
    const dockerEnv = this.getEnvironmentVariable('DOCKER_ENVIRONMENT');
    if (dockerEnv === 'true') {
      return this.getDockerEndpoints();
    }

    // If running in Docker or accessing from container name, use service names
    if (hostname === 'api-gateway' || hostname.includes('docker') || hostname === '127.0.0.1' && this.isDockerEnvironment()) {
      return this.getDockerEndpoints();
    }

    // Default to development/localhost endpoints
    return this.getLocalEndpoints();
  }

  /**
   * Gets environment variable from window or meta tag
   */
  private getEnvironmentVariable(name: string): string | null {
    // Check for meta tag (injected at build time)
    const meta = document.querySelector(`meta[name="${name}"]`);
    if (meta) {
      return meta.getAttribute('content');
    }

    // Check window object (if set globally)
    if (typeof window !== 'undefined' && (window as any)[name]) {
      return (window as any)[name];
    }

    return null;
  }

  /**
   * Development environment endpoints (localhost with mapped ports)
   */
  private getLocalEndpoints(): ApiEndpoints {
    return {
      authService: 'http://localhost:8083/auth-service/api/auth',
      patientService: 'http://localhost:8081/patient-service/api',
      employeeService: 'http://localhost:8082/employee-service/api'
    };
  }

  /**
   * Docker environment endpoints (using service names)
   */
  private getDockerEndpoints(): ApiEndpoints {
    return {
      authService: 'http://auth-service:8083/auth-service/api/auth',
      patientService: 'http://patient-service:8081/patient-service/api',
      employeeService: 'http://employee-service:8082/employee-service/api'
    };
  }

  /**
   * Checks if running in Docker environment by detecting various Docker signals
   */
  private isDockerEnvironment(): boolean {
    // Check for Docker-specific headers or environment signals
    const headers = document.querySelector('meta[http-equiv="X-Docker"]');
    return !!headers || /docker|kubernetes/i.test(navigator.userAgent);
  }

  /**
   * Get all configured endpoints
   */
  getEndpoints(): ApiEndpoints {
    return this.endpoints;
  }

  /**
   * Get specific service endpoint
   */
  getAuthServiceUrl(): string {
    return this.endpoints.authService;
  }

  getPatientServiceUrl(): string {
    return this.endpoints.patientService;
  }

  getEmployeeServiceUrl(): string {
    return this.endpoints.employeeService;
  }

  /**
   * Override endpoints at runtime (useful for testing)
   */
  setEndpoints(endpoints: Partial<ApiEndpoints>): void {
    this.endpoints = { ...this.endpoints, ...endpoints };
  }
}
