package com.sanketika.course_backend.controllers;

import com.sanketika.course_backend.dto.CourseDto;
import com.sanketika.course_backend.mapper.ResponseMapper;
import com.sanketika.course_backend.services.CourseService;
import com.sanketika.course_backend.utils.ApiEnvelope;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    private CourseService courseService;

    @Autowired
private HttpServletRequest request;

private String autoId() {
    String path = request.getServletPath();  // e.g. /api/courses/get
    return path.replace("/", ".").substring(1); // api.courses.get
}


    @GetMapping("/get/live")
    public ResponseEntity<ApiEnvelope<Page<CourseDto>>> getLiveCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        logger.info("📗 Fetching LIVE courses only - page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<CourseDto> liveCourses = courseService.getLiveCourses(pageable);

        ApiEnvelope<Page<CourseDto>> response = ResponseMapper.success(
                autoId(),
                "Live courses fetched successfully",
                liveCourses
        );

        return ResponseEntity.ok(response);
    }

   @GetMapping("/get")
public ResponseEntity<ApiEnvelope<Page<CourseDto>>> getAllCourses(
        @RequestParam(required = false) String text,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size) {

    Pageable pageable = PageRequest.of(page, size);

    if (text == null || text.trim().isEmpty()) {
        Page<CourseDto> allCourses = courseService.getAllCourses(pageable);
        return ResponseEntity.ok(
                ResponseMapper.success(autoId(),
                        "All courses fetched successfully",
                        allCourses)
        );
    }

    List<CourseDto> allCoursesList = courseService
            .getAllCourses(PageRequest.of(0, Integer.MAX_VALUE))  // fetch ALL records
            .getContent();                                        // full list

    String lower = text.toLowerCase();

    List<CourseDto> filtered = allCoursesList.stream()
            .filter(c ->
                    (c.getName() != null && c.getName().toLowerCase().contains(lower)) ||
                    (c.getDescription() != null && c.getDescription().toLowerCase().contains(lower))
            )
            .toList();

    int start = page * size;
    int end = Math.min(start + size, filtered.size());

    List<CourseDto> pageContent =
            start < filtered.size() ? filtered.subList(start, end) : List.of();

    Page<CourseDto> paginatedResult = new PageImpl<>(pageContent, pageable, filtered.size());

    return ResponseEntity.ok(
            ResponseMapper.success(autoId(),
                    "Search results fetched successfully",
                    paginatedResult)
    );
}

    @GetMapping("/get/{id}")
    public ResponseEntity<ApiEnvelope<CourseDto>> getCourseById(@PathVariable UUID id) {
        logger.info("📘 Fetching course with ID: {}", id);

        CourseDto course = courseService.getCourseById(id);

        ApiEnvelope<CourseDto> response = ResponseMapper.success(
                autoId(),
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
                autoId(),
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
                autoId(),
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
                autoId(),
                "Course deleted successfully",
                null
        );

        logger.info("✅ Course deleted: {}", id);
        return ResponseEntity.ok(response);
    }
}
