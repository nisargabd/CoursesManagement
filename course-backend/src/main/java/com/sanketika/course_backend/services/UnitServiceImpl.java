package com.sanketika.course_backend.services;

import com.sanketika.course_backend.dto.UnitDto;
import com.sanketika.course_backend.entity.Course;
import com.sanketika.course_backend.entity.Unit;
import com.sanketika.course_backend.exceptions.ResourceNotFoundException;
import com.sanketika.course_backend.mapper.UnitMapper;
import com.sanketika.course_backend.repositories.CourseRepository;
import com.sanketika.course_backend.repositories.UnitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UnitServiceImpl implements UnitService {
    private static final Logger logger = LoggerFactory.getLogger(UnitService.class);

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UnitMapper unitMapper;

    @Override
    public List<UnitDto> getAllUnits() {
        return unitRepository.findAll().stream()
                .map(unitMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UnitDto> getUnitsByCourse(UUID courseId) {
        return unitRepository.findByCourseId(courseId).stream()
                .map(unitMapper::toDto)
                .collect(Collectors.toList());
    }
@Cacheable(value = "units",key = "#id")
    @Override
    public UnitDto getUnitById(UUID id) {
            Unit unit = unitRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Unit not found"));
    logger.info("Fetching course from DB with id {}", id);
            return unitMapper.toDto(unit);
        }
@CachePut(value = "units",key = "#id")
    @Override
    public UnitDto updateUnit(UUID id, UnitDto dto) {
        Unit existing = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found"));

        existing.setTitle(dto.getTitle());
        existing.setContent(dto.getContent());

        if (dto.getCourseId() != null) {
            Course course = courseRepository.findById(dto.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
            existing.setCourse(course);
        }

        Unit updated = unitRepository.save(existing);
        logger.info("Updating course with id {}",id);
        return unitMapper.toDto(updated);
    }

    @Override
    public UnitDto createUnit(UnitDto dto){
        Unit unit = unitMapper.toEntity(dto);
        
        // Set course relationship if courseId is provided
        if (dto.getCourseId() != null) {
            Course course = courseRepository.findById(dto.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
            unit.setCourse(course);
        }
        
        Unit saved = unitRepository.save(unit);
        logger.info("Created new unit with id {}", saved.getId());
        return unitMapper.toDto(saved);
    }
@CacheEvict(value = "units",key = "#id")
    @Override
    public void deleteUnit(UUID id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found"));
    logger.info("Deleting course with id {}", id);
        unitRepository.delete(unit);

    }
}
