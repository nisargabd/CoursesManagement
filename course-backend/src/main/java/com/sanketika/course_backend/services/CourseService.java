package com.sanketika.course_backend.services;

import com.sanketika.course_backend.dto.CourseDto;
import com.sanketika.course_backend.dto.CourseListRequest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface CourseService {


    Object getCourseById(UUID id);

    CourseDto createCourse(CourseDto dto);

    CourseDto updateCourse(UUID id, CourseDto dto);

    void deleteCourse(UUID id);
    Page<CourseDto> listCourses(CourseListRequest request);
}
