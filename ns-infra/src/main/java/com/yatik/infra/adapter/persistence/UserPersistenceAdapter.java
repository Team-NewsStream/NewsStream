package com.yatik.infra.adapter.persistence;

import com.yatik.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.yatik.infra.repository.SpringDataUserRepository;
import com.yatik.domain.repository.UserRepository;

import java.util.Optional;

/**
 * Adapter implementation of the {@link UserRepository} interface responsible for persistence operations
 * related to the {@link User} entity. This class acts as a bridge between the application's domain layer
 * and the data access layer by delegating database operations to the {@link SpringDataUserRepository}.
 *
 * The adapter uses Spring Data JPA for managing CRUD operations for the {@link User} entity, ensuring a
 * separation of concerns between the domain and persistence layers.
 *
 * This class uses Dependency Injection to integrate with the Spring Data repository and provides
 * concrete implementations for methods defined in the {@link UserRepository} interface.
 */
@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepository {

    private final SpringDataUserRepository jpaRepository;

    @Override
    public User save(User user) {
        return jpaRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public void delete(User user) {
        jpaRepository.delete(user);
    }
}
