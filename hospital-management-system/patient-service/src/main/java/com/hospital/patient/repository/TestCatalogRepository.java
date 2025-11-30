package com.hospital.patient.repository;

import com.hospital.patient.entity.TestCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestCatalogRepository extends JpaRepository<TestCatalog, Long> {

    Optional<TestCatalog> findByTestName(String testName);

    List<TestCatalog> findByCategory(String category);

    List<TestCatalog> findByIsActive(Boolean isActive);

    List<TestCatalog> findByCategoryAndIsActive(String category, Boolean isActive);
}
