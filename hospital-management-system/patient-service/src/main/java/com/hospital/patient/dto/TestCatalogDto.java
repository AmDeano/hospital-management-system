package com.hospital.patient.dto;

import com.hospital.patient.entity.TestCatalog;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCatalogDto {
    private Long id;
    private String testName;
    private String description;
    private String category;
    private String normalRange;
    private String unit;
    private Boolean isActive;

    public static TestCatalogDto fromEntity(TestCatalog catalog) {
        return TestCatalogDto.builder()
                .id(catalog.getId())
                .testName(catalog.getTestName())
                .description(catalog.getDescription())
                .category(catalog.getCategory())
                .normalRange(catalog.getNormalRange())
                .unit(catalog.getUnit())
                .isActive(catalog.getIsActive())
                .build();
    }

    public TestCatalog toEntity() {
        return TestCatalog.builder()
                .testName(this.testName)
                .description(this.description)
                .category(this.category)
                .normalRange(this.normalRange)
                .unit(this.unit)
                .isActive(this.isActive != null ? this.isActive : true)
                .build();
    }
}
