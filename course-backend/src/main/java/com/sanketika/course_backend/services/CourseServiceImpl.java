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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.RedisConnectionFailureException;
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

    @Autowired
    private AuthService authService;

    // ✅ Get only live courses
    @Override
    public Page<CourseDto> getLiveCourses(Pageable pageable) {
        return courseRepository.findByStatus("live", pageable)
                .map(courseMapper::toDto);
    }

    public List<String> getAllBoards() {
        return courseRepository.findDistinctBoards();
    }

    public List<String> getMediumsByBoard(String board) {
        return courseRepository.findDistinctMediumByBoard(board);
    }

    public List<String> getGradesByBoardAndMedium(String board, List<String> mediums) {
        return courseRepository.findDistinctGradeByBoardAndMediums(board, mediums);
    }

    public List<String> getSubjectsByBoardMediumAndGrade(String board, List<String> mediums, List<String> grades) {
        return courseRepository.findDistinctSubjectsByBoardMediumsAndGrades(board, mediums, grades);
    }

    // ✅ Fetch all courses (PAGINATED) with Redis cache & fallback
    @Override
@Cacheable(value = "allCourses", key = "#p.pageNumber")
public Page<CourseDto> getAllCourses(Pageable p) {
    try {
        logger.info("🗂 Fetching all courses (checking cache/DB)...");
        Page<Course> page = courseRepository.findAll(p);
        logger.info("🔍 Fetched {} courses from DB!", page.getTotalElements());
        return page.map(courseMapper::toDto);
    } catch (RedisConnectionFailureException e) {
        logger.warn("⚠️ Redis unavailable, serving data directly from DB: {}", e.getMessage());
        Page<Course> page = courseRepository.findAll(p);
        return page.map(courseMapper::toDto);
    }
}


    // ✅ Get single course by ID (with Redis fallback)
    @Override
    @Cacheable(value = "courses", key = "#id")
    public CourseDto getCourseById(UUID id) {
        try {
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));
            logger.info("📘 Fetched course from DB with ID {}", id);
            return courseMapper.toDto(course);
        } catch (RedisConnectionFailureException e) {
            logger.warn("⚠️ Redis unavailable, reading directly from DB: {}", e.getMessage());
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));
            return courseMapper.toDto(course);
        }
    }

    // ✅ Create new course
   @Override
@CacheEvict(value = {"allCourses", "courses"}, allEntries = true)
public CourseDto createCourse(CourseDto dto) {
    try {
        logger.info("🆕 Creating new course: {}", dto.getName());

        Course course = new Course();
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setBoard(dto.getBoard());
        course.setMedium(dto.getMedium());
        course.setGrade(dto.getGrade());
        course.setSubject(dto.getSubject());
        course.setStatus(dto.getStatus() != null ? dto.getStatus() : "live");

        Course savedCourse = courseRepository.save(course);
        logger.info("✅ Course saved with ID: {}", savedCourse.getId());

        if (dto.getUnits() != null && !dto.getUnits().isEmpty()) {
            List<Unit> unitsToAssociate = new ArrayList<>();
            for (UnitDto unitDto : dto.getUnits()) {
                Unit unit = new Unit();
                unit.setTitle(unitDto.getTitle());
                unit.setContent(unitDto.getContent());
                unit.setCourse(savedCourse);
                unitsToAssociate.add(unit);
            }
            unitRepository.saveAll(unitsToAssociate);
            savedCourse.setUnits(unitsToAssociate);
        }

        logger.info("🧹 Cleared course caches (allCourses, courses)");
        return courseMapper.toDto(savedCourse);
    } catch (Exception ex) {
        logger.error("❌ Error creating course: {}", ex.getMessage(), ex);
        throw ex;
    }
}




    // ✅ Update course & refresh cache
 @Override
@CacheEvict(value = {"courses", "allCourses"}, allEntries = true)
public CourseDto updateCourse(UUID id, CourseDto dto) {
    try {
        logger.info("✏️ Updating course with ID: {}", id);

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
        logger.info("✅ Updated course with ID {}", id);

        return courseMapper.toDto(updated);
    } catch (Exception ex) {
        logger.error("❌ Error updating course {}: {}", id, ex.getMessage(), ex);
        throw ex;
    }
}

    // ✅ Delete course & safely evict cache
    @Override
@CacheEvict(value = {"courses", "allCourses"}, allEntries = true)
public void deleteCourse(UUID courseId) {
    try {
        logger.warn("🗑️ Attempting to delete course with ID: {}", courseId);

        // ✅ Check if course exists
        if (!courseRepository.existsById(courseId)) {
            logger.warn("⚠️ Course with ID {} not found. Skipping deletion.", courseId);
            return; // Do not throw exception — make it idempotent
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

        // ✅ Detach and delete related units first
        List<Unit> units = unitRepository.findByCourseId(courseId);
        if (!units.isEmpty()) {
            units.forEach(u -> u.setCourse(null));
            unitRepository.saveAllAndFlush(units);
            unitRepository.deleteAll(units);
        }

        // ✅ Delete the course
        courseRepository.delete(course);

        // ✅ Log and cache cleanup
        logger.info("✅ Course deleted successfully with ID {}", courseId);

    } catch (RedisConnectionFailureException e) {
        logger.warn("⚠️ Redis unavailable during deletion for course {}: {}", courseId, e.getMessage());
    } catch (Exception ex) {
        logger.error("❌ Unexpected error while deleting course {}", courseId, ex);
        throw ex;
    }
}

}
