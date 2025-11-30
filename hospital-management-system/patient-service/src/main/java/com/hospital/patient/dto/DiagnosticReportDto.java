package com.hospital.patient.dto;

import com.hospital.patient.entity.DiagnosticReport;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosticReportDto {
    private Long id;
    private String patientId;
    private Long doctorId;
    private Long testId;
    private String resultValue;
    private LocalDateTime reportDate;
    private DiagnosticReport.ReportStatus status;
    private String notes;
    private String attachmentUrl;
    private LocalDateTime createdAt;

    public static DiagnosticReportDto fromEntity(DiagnosticReport report) {
        return DiagnosticReportDto.builder()
                .id(report.getId())
                .patientId(report.getPatientId())
                .doctorId(report.getDoctorId())
                .testId(report.getTestId())
                .resultValue(report.getResultValue())
                .reportDate(report.getReportDate())
                .status(report.getStatus())
                .notes(report.getNotes())
                .attachmentUrl(report.getAttachmentUrl())
                .createdAt(report.getCreatedAt())
                .build();
    }

    public DiagnosticReport toEntity() {
        return DiagnosticReport.builder()
                .patientId(this.patientId)
                .doctorId(this.doctorId)
                .testId(this.testId)
                .resultValue(this.resultValue)
                .reportDate(this.reportDate)
                .status(this.status != null ? this.status : DiagnosticReport.ReportStatus.PENDING)
                .notes(this.notes)
                .attachmentUrl(this.attachmentUrl)
                .build();
    }
}
