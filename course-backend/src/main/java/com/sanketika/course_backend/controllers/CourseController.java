package com.sanketika.course_backend.controllers;

import com.sanketika.course_backend.dto.CourseDto;
import com.sanketika.course_backend.mapper.ResponseMapper;
import com.sanketika.course_backend.services.CourseService;
import com.sanketika.course_backend.utils.ApiEnvelope;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin(origins = {"http://localhost:4800", "http://localhost:4200"})
@RequestMapping("/api/courses")
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    private CourseService courseService;


    @GetMapping("/get/live")
    public ResponseEntity<ApiEnvelope<Page<CourseDto>>> getLiveCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        logger.info("📗 Fetching LIVE courses only - page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<CourseDto> liveCourses = courseService.getLiveCourses(pageable);

        ApiEnvelope<Page<CourseDto>> response = ResponseMapper.success(
                "api.course.public",
                "Live courses fetched successfully",
                liveCourses
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get")
    public ResponseEntity<ApiEnvelope<Page<CourseDto>>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        logger.info("📘 Request received to fetch ALL courses - page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<CourseDto> allCoursesPage = courseService.getAllCourses(pageable);

        ApiEnvelope<Page<CourseDto>> response = ResponseMapper.success(
                "api.course.getAll",
                "All courses fetched successfully",
                allCoursesPage
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ApiEnvelope<CourseDto>> getCourseById(@PathVariable UUID id) {
        logger.info("📘 Fetching course with ID: {}", id);

        CourseDto course = courseService.getCourseById(id);

        ApiEnvelope<CourseDto> response = ResponseMapper.success(
                "api.course.get",
                "Course fetched successfully",
                course
        );

        logger.info("✅ Successfully fetched course: {}", id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<ApiEnvelope<CourseDto>> createCourse(@Valid @RequestBody CourseDto dto) {
        logger.info("🆕 Creating new course: {}", dto.getName());

        CourseDto created = courseService.createCourse(dto);

        ApiEnvelope<CourseDto> response = ResponseMapper.success(
                "api.course.create",
                "Course created successfully",
                created
        );

        logger.info("✅ Course created successfully with ID: {}", created.getId());
        return ResponseEntity.ok(response);
    }

   
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiEnvelope<CourseDto>> updateCourse(
            @PathVariable UUID id,
            @Valid @RequestBody CourseDto dto) {

        logger.info("📝 Updating course with ID: {}", id);

        CourseDto updated = courseService.updateCourse(id, dto);

        ApiEnvelope<CourseDto> response = ResponseMapper.success(
                "api.course.update",
                "Course updated successfully",
                updated
        );

        logger.info("✅ Course updated successfully: {}", id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiEnvelope<Void>> deleteCourse(@PathVariable UUID id) {
        logger.warn("🗑️ Deleting course with ID: {}", id);

        courseService.deleteCourse(id);

        ApiEnvelope<Void> response = ResponseMapper.success(
                "api.course.delete",
                "Course deleted successfully",
                null
        );

        logger.info("✅ Course deleted: {}", id);
        return ResponseEntity.ok(response);
    }
}
