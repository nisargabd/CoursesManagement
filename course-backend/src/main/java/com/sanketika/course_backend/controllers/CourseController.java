package com.sanketika.course_backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanketika.course_backend.dto.CourseDto;
import com.sanketika.course_backend.dto.CourseListRequest;
import com.sanketika.course_backend.mapper.ResponseMapper;
import com.sanketika.course_backend.services.CourseService;
import com.sanketika.course_backend.utils.ApiEnvelope;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ObjectMapper redisObjectMapper;

    private String autoId() {
        String path = request.getServletPath();
        return path.replace("/", ".").substring(1);
    }

    @PostMapping("/list")
    public ResponseEntity<ApiEnvelope<Page<CourseDto>>> listCourses(@RequestBody CourseListRequest requestBody) {
        Page<CourseDto> page = courseService.listCourses(requestBody);
        return ResponseEntity.ok(ResponseMapper.success(autoId(), "Courses fetched successfully", page));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ApiEnvelope<CourseDto>> getCourseById(@PathVariable UUID id) {

        Object raw = courseService.getCourseById(id);
        CourseDto course = redisObjectMapper.convertValue(
                raw,
                CourseDto.class);

        return ResponseEntity.ok(
                ResponseMapper.success(autoId(), "Course fetched successfully", course));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<ApiEnvelope<CourseDto>> createCourse(@Valid @RequestBody CourseDto dto) {
        CourseDto created = courseService.createCourse(dto);
        return ResponseEntity.ok(
                ResponseMapper.success(autoId(), "Course created successfully", created));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiEnvelope<CourseDto>> updateCourse(
            @PathVariable UUID id,
            @Valid @RequestBody CourseDto dto) {

        CourseDto updated = courseService.updateCourse(id, dto);
        return ResponseEntity.ok(
                ResponseMapper.success(autoId(), "Course updated successfully", updated));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiEnvelope<Void>> deleteCourse(@PathVariable UUID id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(
                ResponseMapper.success(autoId(), "Course deleted successfully", null));
    }
}
