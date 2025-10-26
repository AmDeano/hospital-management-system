package com.hospital.auth.service;

import org.springframework.stereotype.Service;

import com.hospital.auth.entity.Role;

@Service
public class DashboardService {

	public String getDashboardRoute(Role role) {
		if (role == null) 
			return "/dashboard";
		return switch (role) {
		case ADMIN -> "/dashboard/admin";
		case HR -> "/dashboard/hr";
		case DOCTOR -> "/dashboard/doctor";
		case NURSE -> "/dashboard/nurse";
		case RECEPTIONIST -> "/dashboard/receptionist";
		case OBSERVATOR -> "/dashboard/observator";
		case SUPERVISOR -> "/dashboard/supervisor";
		case PATIENT -> "/dashboard/patient";
		};
	}
}
