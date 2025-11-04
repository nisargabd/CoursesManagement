package com.sanketika.course_backend.repositories;

// import com.sanketika.course_backend.dto.CourseDto;
import com.sanketika.course_backend.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID>, JpaSpecificationExecutor<Course> {

    Page<Course> findAll(Pageable pageable);

    @Query("SELECT DISTINCT c.board from Course c")
    List<String> findDistinctBoards();

    @Query("Select distinct c.medium from Course c where c.board=:board")
    List<String> findDistinctMediumByBoard(@Param("board") String board);

    @Query(
  value = "SELECT DISTINCT grade FROM courses " +
          "WHERE board = :board " +
          "AND EXISTS (SELECT 1 FROM jsonb_array_elements_text(medium::jsonb) AS m WHERE m IN (:mediums))",
  nativeQuery = true
)
List<String> findDistinctGradeByBoardAndMediums(
        @Param("board") String board,
        @Param("mediums") List<String> mediums
);



//    @Query("SELECT DISTINCT c.grade FROM Course c WHERE c.board = :board AND c.medium = :medium")
//    List<String> findDistinctGradeByBoardAndMedium(@Param("board") String board,@Param("medium") String medium);

//     @Query("SELECT DISTINCT c.subject from Course c where c.board=:board and c.medium=:medium")
//     List<String> findDistinctSubjectByBoardAndMediumAndGrade(@Param("board") String board,
//                                                              @Param("medium") String medium,
//                                                              @Param("grade") String grade);

@Query(
  value = "SELECT DISTINCT subject " +
          "FROM courses " +
          "WHERE board = :board " +
          "AND EXISTS (SELECT 1 FROM jsonb_array_elements_text(medium::jsonb) AS m WHERE m IN (:mediums)) " +
          "AND EXISTS (SELECT 1 FROM jsonb_array_elements_text(grade::jsonb) AS g WHERE g IN (:grades))",
  nativeQuery = true
)
List<String> findDistinctSubjectsByBoardMediumsAndGrades(
        @Param("board") String board,
        @Param("mediums") List<String> mediums,
        @Param("grades") List<String> grades
);


}
