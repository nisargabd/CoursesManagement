package com.sanketika.repositories;

import com.sanketika.entity.User;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import play.db.jpa.JPAApi;

import java.util.Optional;
import java.util.UUID;

public class UserRepository {

    private final JPAApi jpaApi;

    @Inject
    public UserRepository(JPAApi jpaApi) {
        this.jpaApi = jpaApi;
    }

    public Optional<User> findById(UUID id) {
        return jpaApi.withTransaction(em -> {
            User user = em.find(User.class, id);
            return Optional.ofNullable(user);
        });
    }

    public User save(User user) {
        return jpaApi.withTransaction(em -> {
            if (user.getId() == null) {
                em.persist(user);
                return user;
            } else {
                return em.merge(user);
            }
        });
    }

    public Optional<User> findByEmail(String email) {
        return jpaApi.withTransaction(em -> {
            try {
                User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                        .setParameter("email", email)
                        .getSingleResult();
                return Optional.of(user);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    public boolean existsByEmail(String email) {
        return jpaApi.withTransaction(em -> {
            Long count = em.createQuery("SELECT count(u) FROM User u WHERE u.email = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return count > 0;
        });
    }

    public boolean existsByUsername(String username) {
        return jpaApi.withTransaction(em -> {
            Long count = em.createQuery("SELECT count(u) FROM User u WHERE u.username = :username", Long.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return count > 0;
        });
    }

    public boolean existsByPhone(String phone) {
        return jpaApi.withTransaction(em -> {
            Long count = em.createQuery("SELECT count(u) FROM User u WHERE u.phone = :phone", Long.class)
                    .setParameter("phone", phone)
                    .getSingleResult();
            return count > 0;
        });
    }
}
