package com.sanketika.course_backend.services;

import com.sanketika.course_backend.dto.CourseDto;
// import com.sanketika.course_backend.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// import java.util.List;
import java.util.UUID;

public interface CourseService {
     Page<CourseDto> getAllCourses(Pageable p);


     CourseDto getCourseById(UUID id);
     CourseDto createCourse(CourseDto dto);
     CourseDto updateCourse(UUID id,CourseDto dto);
     void deleteCourse(UUID id);
//     List<Course> findAllFiltered(String q, String board, String medium, String grade, String subject);

}
