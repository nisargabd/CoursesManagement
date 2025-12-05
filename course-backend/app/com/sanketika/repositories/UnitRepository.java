package com.sanketika.repositories;

import com.sanketika.entity.Unit;
import jakarta.inject.Inject;
import play.db.jpa.JPAApi;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UnitRepository {

    private final JPAApi jpaApi;

    @Inject
    public UnitRepository(JPAApi jpaApi) {
        this.jpaApi = jpaApi;
    }

    public Unit save(Unit unit) {
        return jpaApi.withTransaction(em -> {
            if (unit.getId() == null) {
                em.persist(unit);
                return unit;
            } else {
                return em.merge(unit);
            }
        });
    }

    public Optional<Unit> findById(UUID id) {
        return jpaApi.withTransaction(em -> {
            Unit unit = em.find(Unit.class, id);
            return Optional.ofNullable(unit);
        });
    }

    public List<Unit> findByCourseId(UUID courseId) {
        return jpaApi.withTransaction(em -> {
            return em.createQuery("SELECT u FROM Unit u WHERE u.course.id = :courseId", Unit.class)
                    .setParameter("courseId", courseId)
                    .getResultList();
        });
    }
    
    public void delete(Unit unit) {
        jpaApi.withTransaction(em -> {
            if (em.contains(unit)) {
                em.remove(unit);
            } else {
                em.remove(em.merge(unit));
            }
        });
    }
}
