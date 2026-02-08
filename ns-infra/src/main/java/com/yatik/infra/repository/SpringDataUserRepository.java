package com.yatik.infra.repository;

import com.yatik.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities using Spring Data JPA.
 * This interface provides methods for CRUD operations and additional query methods
 * to work with {@link User} entities based on their email attribute.
 *
 * <br> Extends {@link JpaRepository} to inherit several standard persistence methods.
 */
@Repository
public interface SpringDataUserRepository extends JpaRepository<User, String> {
    // Spring generates SQL for this.
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
