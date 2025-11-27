package com.sanketika.course_backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanketika.course_backend.dto.CourseDto;
import com.sanketika.course_backend.dto.CourseListRequest;
import com.sanketika.course_backend.dto.PageResponse;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

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
    public ResponseEntity<ApiEnvelope<PageResponse<CourseDto>>> listCourses(@RequestBody CourseListRequest requestBody) {
        logger.debug("=== listCourses endpoint called ===");
        logger.debug("Request body: {}", requestBody);
        
        Page<CourseDto> page = courseService.listCourses(requestBody);
        logger.debug("Service returned page with {} elements, total elements: {}", 
                     page.getNumberOfElements(), page.getTotalElements());
        
        // Convert Page to PageResponse for proper JSON serialization
        PageResponse<CourseDto> pageResponse = PageResponse.from(page);
        logger.debug("PageResponse created with {} content items", pageResponse.getContent().size());
        
        ApiEnvelope<PageResponse<CourseDto>> response = ResponseMapper.success(autoId(), "Courses fetched successfully", pageResponse);
        logger.debug("Response envelope created: id={}, responseCode={}", 
                     response.getId(), response.getResponseCode());
        logger.debug("Response result: {}", response.getResult());
        
        return ResponseEntity.ok(response);
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
