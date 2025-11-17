package com.sanketika.course_backend.repositories;

import com.sanketika.course_backend.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID>, JpaSpecificationExecutor<Course> {

    Page<Course> findByStatus(String status, Pageable pageable);

    Page<Course> findByDeletedFalse(Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.deleted = false ORDER BY c.createdAt DESC")
Page<Course> findActiveCourses(Pageable pageable);


    
    Optional<Course> findById(UUID id);

    @Query("SELECT DISTINCT c.board from Course c")
    List<String> findDistinctBoards();

    @Query("Select distinct c.medium from Course c where c.board=:board")
    List<String> findDistinctMediumByBoard(@Param("board") String board);

    @Query(
        value = "SELECT DISTINCT grade FROM courses " +
                "WHERE board = :board " +
                "AND exists (select 1 from jsonb_array_elements_text(medium::jsonb) as m where m IN (:mediums))",
        nativeQuery = true
    )
    List<String> findDistinctGradeByBoardAndMediums(
            @Param("board") String board,
            @Param("mediums") List<String> mediums
    );

    @Query(
        value = "SELECT DISTINCT subject " +
                "FROM courses " +
                "WHERE board = :board " +
                "AND exists (select 1 from jsonb_array_elements_text(medium::jsonb) as m where m IN (:mediums)) " +
                "AND exists (select 1 from jsonb_array_elements_text(grade::jsonb) as g where g IN (:grades))",
        nativeQuery = true
    )
    List<String> findDistinctSubjectsByBoardMediumsAndGrades(
            @Param("board") String board,
            @Param("mediums") List<String> mediums,
            @Param("grades") List<String> grades
    );

    @Query("SELECT DISTINCT c.medium FROM Course c")
    List<String> findDistinctMediums();

    @Query("SELECT DISTINCT c.grade FROM Course c")
    List<String> findDistinctGrades();

    @Query("SELECT DISTINCT c.subject FROM Course c")
    List<String> findDistinctSubjects();

}
