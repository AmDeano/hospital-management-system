package com.hospital.auth.repo;

import com.hospital.auth.entity.Role;
import com.hospital.auth.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserAccount, Long> {

    // ==================== CORE QUERIES ====================
    
    /**
     * Find user by unique matricule
     */
    Optional<UserAccount> findByMatricule(String matricule);
    
    /**
     * Find user by email
     */
    Optional<UserAccount> findByEmail(String email);
    
    /**
     * Find user by external ID (e.g., from employee service)
     */
    Optional<UserAccount> findByExternalId(String externalId);

    // ==================== EXISTENCE CHECKS ====================
    
    /**
     * Check if matricule exists
     */
    boolean existsByMatricule(String matricule);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if external ID exists
     */
    boolean existsByExternalId(String externalId);

    // ==================== FILTERED QUERIES ====================
    
    /**
     * Find all enabled users
     */
    List<UserAccount> findByEnabledTrue();
    
    /**
     * Find all disabled users
     */
    List<UserAccount> findByEnabledFalse();
    
    /**
     * Find users by enabled status
     */
    List<UserAccount> findByEnabled(boolean enabled);
    
    /**
     * Find users created after a specific date
     */
    List<UserAccount> findByCreatedAtAfter(Instant createdAt);
    
    /**
     * Find users created between dates
     */
    List<UserAccount> findByCreatedAtBetween(Instant startDate, Instant endDate);

    // ==================== ROLE-BASED QUERIES ====================
    
    /**
     * Find users with a specific role
     */
    @Query("SELECT u FROM UserAccount u JOIN u.roles r WHERE r = :role")
    List<UserAccount> findByRole(@Param("role") Role role);
    
    /**
     * Find enabled users with a specific role
     */
    @Query("SELECT u FROM UserAccount u JOIN u.roles r WHERE r = :role AND u.enabled = true")
    List<UserAccount> findEnabledByRole(@Param("role") Role role);
    
    /**
     * Check if user has a specific role
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
           "FROM UserAccount u JOIN u.roles r " +
           "WHERE u.matricule = :matricule AND r = :role")
    boolean hasRole(@Param("matricule") String matricule, @Param("role") Role role);
    
    /**
     * Count users by role
     */
    @Query("SELECT COUNT(DISTINCT u) FROM UserAccount u JOIN u.roles r WHERE r = :role")
    long countByRole(@Param("role") Role role);

    // ==================== SEARCH QUERIES ====================
    
    /**
     * Search users by first name or last name (case-insensitive)
     */
    @Query("SELECT u FROM UserAccount u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<UserAccount> searchByName(@Param("name") String name);
    
    /**
     * Search users by matricule pattern (case-insensitive)
     */
    @Query("SELECT u FROM UserAccount u WHERE LOWER(u.matricule) LIKE LOWER(CONCAT('%', :pattern, '%'))")
    List<UserAccount> searchByMatriculePattern(@Param("pattern") String pattern);
    
    /**
     * Search users by email pattern (case-insensitive)
     */
    @Query("SELECT u FROM UserAccount u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :pattern, '%'))")
    List<UserAccount> searchByEmailPattern(@Param("pattern") String pattern);

    // ==================== STATISTICS ====================
    
    /**
     * Count total enabled users
     */
    long countByEnabledTrue();
    
    /**
     * Count total disabled users
     */
    long countByEnabledFalse();
    
    /**
     * Count users created after a specific date
     */
    long countByCreatedAtAfter(Instant createdAt);

    // ==================== BATCH OPERATIONS ====================
    
    /**
     * Find users by list of matricules
     */
    List<UserAccount> findByMatriculeIn(List<String> matricules);
    
    /**
     * Find users by list of emails
     */
    List<UserAccount> findByEmailIn(List<String> emails);
    
    /**
     * Find users by list of external IDs
     */
    List<UserAccount> findByExternalIdIn(List<String> externalIds);

    // ==================== DELETION ====================
    
    /**
     * Delete user by matricule
     */
    void deleteByMatricule(String matricule);
    
    /**
     * Delete disabled users older than a specific date
     */
    @Query("DELETE FROM UserAccount u WHERE u.enabled = false AND u.createdAt < :date")
    void deleteDisabledUsersOlderThan(@Param("date") Instant date);
    
 // ==================== PATIENT-SPECIFIC QUERIES ====================

    /**
     * Find patient by CIN
     */
    Optional<UserAccount> findByCIN(String cin);

    /**
     * Check if a CIN exists (for adults)
     */
    boolean existsByCIN(String cin);

    /**
     * Find patients by birth date
     */
    List<UserAccount> findByDateNaissance(LocalDate dateNaissance);

    /**
     * Find patients by minor status
     */
    List<UserAccount> findByIsMinor(Boolean isMinor);

    /**
     * Find patients by parent's CIN
     */
    List<UserAccount> findByParentCin(String parentCin);

}