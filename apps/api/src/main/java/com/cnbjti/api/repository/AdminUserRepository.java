package com.cnbjti.api.repository;

import com.cnbjti.api.entity.AdminUserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUserRepository extends JpaRepository<AdminUserEntity, String> {

  Optional<AdminUserEntity> findByUsername(String username);

  Optional<AdminUserEntity> findByUsernameIgnoreCase(String username);

  Optional<AdminUserEntity> findByEmailIgnoreCase(String email);

  long countByRoleAndStatus(String role, String status);
}
