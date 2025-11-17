package com.sanketika.course_backend.services;

import com.sanketika.course_backend.dto.CourseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface CourseService {
     Page<CourseDto> getAllCourses(Pageable pageable);
    Page<CourseDto> getLiveCourses(Pageable pageable);
    CourseDto getCourseById(UUID id);
    CourseDto createCourse(CourseDto dto);
    CourseDto updateCourse(UUID id, CourseDto dto);
    void deleteCourse(UUID courseId);

    

}
