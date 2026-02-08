package com.yatik.domain.repository;

import com.yatik.domain.entity.User;
import java.util.Optional;

public interface UserRepository {

    /**
     * Persists a user (Create or Update).
     * Used for Registration and Password/Profile updates.
     */
    User save(User user);

    /**
     * Finds a user by their unique email (ID).
     * Used extensively during Login (Authentication).
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists.
     * Used during Registration to prevent duplicate accounts.
     */
    boolean existsByEmail(String email);

    void delete(User user);
}