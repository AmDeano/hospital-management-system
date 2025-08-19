// auth-service/src/main/java/com/hospital/auth/repo/UserRepository.java
package com.hospital.auth.repo;

import com.hospital.auth.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserAccount, Long> {
  Optional<UserAccount> findByUsername(String username);
  boolean existsByUsername(String username);
  boolean existsByEmail(String email);
}
