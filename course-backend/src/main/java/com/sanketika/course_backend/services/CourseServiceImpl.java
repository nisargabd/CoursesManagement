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

    @Override
    public Page<CourseDto> getAllCourses(Pageable p) {
        Page<Course> page = courseRepository.findAll(p);
        return page.map(courseMapper::toDto);
    }

    @Override
    @Cacheable(value = "courses", key = "#id")
    public CourseDto getCourseById(UUID id) {
        try {
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));
            logger.info("Fetching course from DB with ID {}", id);
            return courseMapper.toDto(course);
        } catch (ResourceNotFoundException ex) {
            logger.error("Course not found with ID {}: {}", id, ex.getMessage());
            throw ex; // rethrow to be handled by GlobalExceptionHandler
        } catch (Exception ex) {
            logger.error("Unexpected error while fetching course with ID {}", id, ex);
            throw ex;
        }
    }


    @Override
    public CourseDto createCourse(CourseDto dto) {
        try {
            logger.info("Creating new course: {}", dto.getName());
            
            // Create the course entity without units first
            Course course = new Course();
            course.setName(dto.getName());
            course.setDescription(dto.getDescription());
            course.setBoard(dto.getBoard());
            course.setMedium(dto.getMedium());
            course.setGrade(dto.getGrade());
            course.setSubject(dto.getSubject());
            
            // Save the course first to get the ID
            Course savedCourse = courseRepository.save(course);
            logger.info("Course saved with ID: {}", savedCourse.getId());
            
            // Handle units separately
            if (dto.getUnits() != null && !dto.getUnits().isEmpty()) {
                List<Unit> unitsToAssociate = new ArrayList<>();
                
                for (UnitDto unitDto : dto.getUnits()) {
                    if (unitDto.getId() != null) {
                        // This is an existing unit - fetch it and associate with the course
                        try {
                            Unit existingUnit = unitRepository.findById(unitDto.getId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Unit not found with ID: " + unitDto.getId()));
                            
                            // Disconnect from previous course if any
                            existingUnit.setCourse(null);
                            unitRepository.save(existingUnit);
                            
                            // Associate with new course
                            existingUnit.setCourse(savedCourse);
                            unitsToAssociate.add(existingUnit);
                            
                            logger.info("Associated existing unit {} with new course {}", unitDto.getId(), savedCourse.getId());
                        } catch (ResourceNotFoundException ex) {
                            logger.warn("Unit with ID {} not found, skipping: {}", unitDto.getId(), ex.getMessage());
                        }
                    } else {
                        // This is a new unit - create it
                        Unit newUnit = new Unit();
                        newUnit.setTitle(unitDto.getTitle());
                        newUnit.setContent(unitDto.getContent());
                        newUnit.setCourse(savedCourse);
                        unitsToAssociate.add(newUnit);
                        
                        logger.info("Created new unit for course {}", savedCourse.getId());
                    }
                }
                
                // Save all units
                if (!unitsToAssociate.isEmpty()) {
                    unitRepository.saveAll(unitsToAssociate);
                    savedCourse.setUnits(unitsToAssociate);
                    logger.info("Associated {} units with course {}", unitsToAssociate.size(), savedCourse.getId());
                }
            }
            
            logger.info("Course creation completed successfully with ID: {}", savedCourse.getId());
            return courseMapper.toDto(savedCourse);
            
        } catch (Exception ex) {
            logger.error("Error creating course: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    @CachePut(value = "courses", key = "#id")
    public CourseDto updateCourse(UUID id, CourseDto dto) {
        try {
            Course existing = courseRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));

            // Update basic course fields
            existing.setName(dto.getName());
            existing.setDescription(dto.getDescription());
            existing.setBoard(dto.getBoard());
            existing.setMedium(dto.getMedium());
            existing.setGrade(dto.getGrade());
            existing.setSubject(dto.getSubject());

            // Handle units update
            if (dto.getUnits() != null) {
                // Get current units
                List<Unit> currentUnits = existing.getUnits();
                
                // Collect IDs of units from DTO
                List<UUID> dtoUnitIds = dto.getUnits().stream()
                        .map(UnitDto::getId)
                        .filter(unitId -> unitId != null)
                        .toList();

                // Step 1: Disconnect units that are no longer in the DTO
                List<Unit> unitsToRemove = new ArrayList<>();
                for (Unit unit : currentUnits) {
                    if (!dtoUnitIds.contains(unit.getId())) {
                        unit.setCourse(null);
                        unitsToRemove.add(unit);
                    }
                }
                currentUnits.removeAll(unitsToRemove);

                // Step 2: Add or update units from DTO
                for (UnitDto unitDto : dto.getUnits()) {
                    if (unitDto.getId() != null) {
                        // Fetch the unit from database
                        Unit unit = unitRepository.findById(unitDto.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Unit not found with ID: " + unitDto.getId()));
                        
                        // Update unit fields
                        unit.setTitle(unitDto.getTitle());
                        unit.setContent(unitDto.getContent());
                        
                        // Check if unit is already associated with this course
                        boolean alreadyInCourse = currentUnits.stream()
                                .anyMatch(u -> u.getId().equals(unit.getId()));
                        
                        if (!alreadyInCourse) {
                            // New unit for this course - establish bidirectional relationship
                            unit.setCourse(existing);
                            currentUnits.add(unit);
                        } else {
                            // Unit already in course, just ensure the relationship is set
                            unit.setCourse(existing);
                        }
                    }
                }
            }

            Course updated = courseRepository.save(existing);
            logger.info("Updated course successfully with ID {} including {} units", id, updated.getUnits().size());
            return courseMapper.toDto(updated);

        } catch (ResourceNotFoundException ex) {
            logger.error("Cannot update course, not found ID {}: {}", id, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error while updating course with ID {}", id, ex);
            throw ex;
        }
    }

    @Override
    @CacheEvict(value = "courses", key = "#courseId")
    public void deleteCourse(UUID courseId) {
        try {
            logger.warn("Deleting course with ID: {}", courseId);

            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

            List<Unit> units = unitRepository.findByCourseId(courseId);
            if (!units.isEmpty()) {
                for (Unit u : units) {
                    u.setCourse(null);
                }
                unitRepository.saveAllAndFlush(units);
            }
            course.setUnits(null);
            courseRepository.delete(course);

            logger.info("Course deleted successfully with ID {}", courseId);

        } catch (ResourceNotFoundException ex) {
            logger.error("Cannot delete course, not found ID {}: {}", courseId, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error while deleting course with ID {}", courseId, ex);
            throw ex;
        }
    }


}
