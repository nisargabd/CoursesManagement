package com.sanketika.repositories;

import com.sanketika.entity.Course;
import jakarta.inject.Inject;
import jakarta.persistence.NoResultException;
import play.db.jpa.JPAApi;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CourseRepository {

    private final JPAApi jpaApi;

    @Inject
    public CourseRepository(JPAApi jpaApi) {
        this.jpaApi = jpaApi;
    }

    public Course save(Course course) {
        return jpaApi.withTransaction(em -> {
            if (course.getId() == null) {
                em.persist(course);
                return course;
            } else {
                return em.merge(course);
            }
        });
    }

    public Optional<Course> findById(UUID id) {
        return jpaApi.withTransaction(em -> {
            Course course = em.find(Course.class, id);
            return Optional.ofNullable(course);
        });
    }

    public void delete(Course course) {
        jpaApi.withTransaction(em -> {
            if (em.contains(course)) {
                em.remove(course);
            } else {
                em.remove(em.merge(course));
            }
        });
    }

    public List<Course> findByStatus(String status) {
        return jpaApi.withTransaction(em -> {
            return em.createQuery("SELECT c FROM Course c WHERE c.status = :status", Course.class)
                    .setParameter("status", status)
                    .getResultList();
        });
    }

    public List<Course> findActiveCourses() {
        return jpaApi.withTransaction(em -> {
            return em.createQuery("SELECT c FROM Course c WHERE c.deleted = false ORDER BY c.createdAt DESC", Course.class)
                    .getResultList();
        });
    }

    public List<String> findDistinctBoards() {
        return jpaApi.withTransaction(em -> {
            return em.createQuery("SELECT DISTINCT c.board from Course c", String.class)
                    .getResultList();
        });
    }

    public List<String> findDistinctMediumByBoard(String board) {
        return jpaApi.withTransaction(em -> {
            return em.createQuery("Select distinct c.medium from Course c where c.board=:board", String.class)
                    .setParameter("board", board)
                    .getResultList();
        });
    }

    @SuppressWarnings("unchecked")
    public List<String> findDistinctGradeByBoardAndMediums(String board, List<String> mediums) {
        return jpaApi.withTransaction(em -> {
            String sql = "SELECT DISTINCT grade FROM courses " +
                    "WHERE board = :board " +
                    "AND exists (select 1 from jsonb_array_elements_text(medium::jsonb) as m where m IN (:mediums))";
            return em.createNativeQuery(sql)
                    .setParameter("board", board)
                    .setParameter("mediums", mediums)
                    .getResultList();
        });
    }

    @SuppressWarnings("unchecked")
    public List<String> findDistinctSubjectsByBoardMediumsAndGrades(String board, List<String> mediums, List<String> grades) {
        return jpaApi.withTransaction(em -> {
            String sql = "SELECT DISTINCT subject " +
                    "FROM courses " +
                    "WHERE board = :board " +
                    "AND exists (select 1 from jsonb_array_elements_text(medium::jsonb) as m where m IN (:mediums)) " +
                    "AND exists (select 1 from jsonb_array_elements_text(grade::jsonb) as g where g IN (:grades))";
            return em.createNativeQuery(sql)
                    .setParameter("board", board)
                    .setParameter("mediums", mediums)
                    .setParameter("grades", grades)
                    .getResultList();
        });
    }

    public List<String> findDistinctMediums() {
        return jpaApi.withTransaction(em -> {
            return em.createQuery("SELECT DISTINCT c.medium FROM Course c", String.class)
                    .getResultList();
        });
    }

    public List<String> findDistinctGrades() {
        return jpaApi.withTransaction(em -> {
            return em.createQuery("SELECT DISTINCT c.grade FROM Course c", String.class)
                    .getResultList();
        });
    }

    public List<String> findDistinctSubjects() {
        return jpaApi.withTransaction(em -> {
            return em.createQuery("SELECT DISTINCT c.subject FROM Course c", String.class)
                    .getResultList();
        });
    }
}
