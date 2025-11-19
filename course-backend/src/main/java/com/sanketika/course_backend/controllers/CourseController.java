package com.sanketika.course_backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanketika.course_backend.dto.CourseDto;
import com.sanketika.course_backend.mapper.ResponseMapper;
import com.sanketika.course_backend.services.CourseService;
import com.sanketika.course_backend.utils.ApiEnvelope;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
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

    // private static final Logger logger =
    // LoggerFactory.getLogger(CourseController.class);

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

    @GetMapping("/get/live")
    public ResponseEntity<ApiEnvelope<Page<CourseDto>>> getLiveCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String text) {

        Pageable pageable = PageRequest.of(page, size);

        List<?> raw = courseService.getLiveCourses();

        List<CourseDto> allLiveCourses = redisObjectMapper.convertValue(
                raw,
                redisObjectMapper.getTypeFactory().constructCollectionType(List.class,
                        CourseDto.class));

        // FILTERING
        List<CourseDto> filtered = (text == null || text.isBlank())
                ? allLiveCourses
                : allLiveCourses.stream()
                        .filter(c -> (c.getName() != null && c
                                .getName().toLowerCase().contains(text.toLowerCase()))
                                ||
                                (c.getDescription() != null && c.getDescription()
                                        .toLowerCase()
                                        .contains(text.toLowerCase())))
                        .toList();

        int start = Math.min(page * size, filtered.size());
        int end = Math.min(start + size, filtered.size());

        Page<CourseDto> paginated = new PageImpl<>(filtered.subList(start, end), pageable, filtered.size());

        return ResponseEntity.ok(
                ResponseMapper.success(autoId(), "Live courses fetched successfully", paginated));
    }

    @GetMapping("/get")
    public ResponseEntity<ApiEnvelope<Page<CourseDto>>> getAllCourses(
            @RequestParam(required = false) String text,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);

        List<?> raw = courseService.getAllCourses();
        List<CourseDto> allCourses = redisObjectMapper.convertValue(
                raw,
                redisObjectMapper.getTypeFactory().constructCollectionType(List.class,
                        CourseDto.class));

        List<CourseDto> filtered = (text == null || text.isBlank())
                ? allCourses
                : allCourses.stream()
                        .filter(c -> (c.getName() != null && c
                                .getName().toLowerCase().contains(text.toLowerCase()))
                                ||
                                (c.getDescription() != null && c.getDescription()
                                        .toLowerCase()
                                        .contains(text.toLowerCase())))
                        .toList();

        int start = Math.min(page * size, filtered.size());
        int end = Math.min(start + size, filtered.size());

        Page<CourseDto> paginated = new PageImpl<>(filtered.subList(start, end), pageable, filtered.size());

        return ResponseEntity.ok(
                ResponseMapper.success(autoId(), "All courses fetched successfully", paginated));
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
