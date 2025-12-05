package com.sanketika.services;

import com.google.inject.ImplementedBy;
import com.sanketika.dto.CourseDto;
import com.sanketika.dto.CourseListRequest;
import com.sanketika.dto.PageResponse;

import java.util.UUID;

@ImplementedBy(CourseServiceImpl.class)
public interface CourseService {

    Object getCourseById(UUID id);

    CourseDto createCourse(CourseDto dto);

    CourseDto updateCourse(UUID id, CourseDto dto);

    void deleteCourse(UUID id);
    
    PageResponse<CourseDto> listCourses(CourseListRequest request);
}
