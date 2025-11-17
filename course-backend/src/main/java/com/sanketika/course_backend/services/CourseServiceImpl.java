package com.sanketika.course_backend.services;

import com.sanketika.course_backend.dto.CourseDto;
import com.sanketika.course_backend.dto.UnitDto;
import com.sanketika.course_backend.entity.Course;
import com.sanketika.course_backend.entity.Unit;
import com.sanketika.course_backend.exceptions.ResourceNotFoundException;
import com.sanketika.course_backend.mapper.CourseMapper;
import com.sanketika.course_backend.repositories.CourseRepository;
import com.sanketika.course_backend.repositories.UnitRepository;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private CourseMapper courseMapper;

    @Override
    @Cacheable(
    value = "course",
    key = "'live:' + #pageable.pageNumber + ':' + #pageable.pageSize"
)
    public Page<CourseDto> getLiveCourses(Pageable pageable) {
        return courseRepository
                .findByStatus("live", pageable)
                .map(courseMapper::toDto);
    }

    @Override
   @Cacheable(
    value = "course",
    key = "'all:' + #pageable.pageNumber + ':' + #pageable.pageSize"
)
    public Page<CourseDto> getAllCourses(Pageable pageable) {
        logger.info("Fetching all non-deleted courses...");
        return courseRepository
                .findActiveCourses(pageable)
                .map(courseMapper::toDto);
    }

    @Override
    @Cacheable(value = "course", key = "#id")
    public CourseDto getCourseById(UUID id) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));

        logger.info("Fetched course (not deleted) ID {}", id);
        return courseMapper.toDto(course);
    }

    @Override
    @CacheEvict(value = "course", allEntries = true) 
    public CourseDto createCourse(CourseDto dto) {
        logger.info("Creating new course: {}", dto.getName());

        Course course = new Course();
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setBoard(dto.getBoard());
        course.setMedium(dto.getMedium());
        course.setGrade(dto.getGrade());
        course.setSubject(dto.getSubject());
        course.setStatus(dto.getStatus() != null ? dto.getStatus() : "live");

        Course savedCourse = courseRepository.save(course);

        if (dto.getUnits() != null && !dto.getUnits().isEmpty()) {
            List<Unit> units = new ArrayList<>();
            for (UnitDto unitDto : dto.getUnits()) {
                Unit unit = new Unit();
                unit.setTitle(unitDto.getTitle());
                unit.setContent(unitDto.getContent());
                unit.setCourse(savedCourse);
                units.add(unit);
            }
            unitRepository.saveAll(units);
            savedCourse.setUnits(units);
        }

        return courseMapper.toDto(savedCourse);
    }
    @Override
    @CacheEvict(value = "course", allEntries = true) 
    public CourseDto updateCourse(UUID id, CourseDto dto) {
        logger.info("Updating course ID {}", id);

        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setBoard(dto.getBoard());
        existing.setMedium(dto.getMedium());
        existing.setGrade(dto.getGrade());
        existing.setSubject(dto.getSubject());
        existing.setStatus(dto.getStatus() != null ? dto.getStatus() : "live");

        Course updated = courseRepository.save(existing);

        return courseMapper.toDto(updated);
    }

    @Override
    @CacheEvict(value = "course", allEntries = true)  
    public void deleteCourse(UUID courseId) {
        logger.warn("Soft deleting course ID: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

        course.setDeleted(true);
        courseRepository.save(course);

        logger.info("Soft deleted course: {}", courseId);
    }

}
