package com.sanketika.course_backend.services;

import com.sanketika.course_backend.dto.CourseDto;

import java.util.List;
import java.util.UUID;

public interface CourseService {

    List<CourseDto> getLiveCourses();   
    List<CourseDto> getAllCourses();       

    Object getCourseById(UUID id);

    CourseDto createCourse(CourseDto dto);

    CourseDto updateCourse(UUID id, CourseDto dto);

    void deleteCourse(UUID id);
}
