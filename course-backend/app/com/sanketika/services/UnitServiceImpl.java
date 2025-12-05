package com.sanketika.services;

import com.sanketika.dto.UnitDto;
import com.sanketika.entity.Course;
import com.sanketika.entity.Unit;
import com.sanketika.exceptions.ResourceNotFoundException;
import com.sanketika.mapper.UnitMapper;
import com.sanketika.repositories.CourseRepository;
import com.sanketika.repositories.UnitRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import play.db.jpa.JPAApi;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;
    private final CourseRepository courseRepository;
    private final UnitMapper unitMapper;
    private final JPAApi jpaApi;

    @Inject
    public UnitServiceImpl(UnitRepository unitRepository,
                           CourseRepository courseRepository,
                           UnitMapper unitMapper,
                           JPAApi jpaApi) {
        this.unitRepository = unitRepository;
        this.courseRepository = courseRepository;
        this.unitMapper = unitMapper;
        this.jpaApi = jpaApi;
    }

    @Override
    public List<UnitDto> getAllUnits() {
        return jpaApi.withTransaction(em -> {
            List<Unit> units = em.createQuery("SELECT u FROM Unit u", Unit.class)
                    .getResultList();
            return units.stream()
                    .map(unitMapper::toDto)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public List<UnitDto> getUnitsByCourse(UUID courseId) {
        return unitRepository.findByCourseId(courseId).stream()
                .map(unitMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UnitDto getUnitById(UUID id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found"));
        return unitMapper.toDto(unit);
    }

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
        return unitMapper.toDto(updated);
    }

    @Override
    public UnitDto createUnit(UnitDto dto) {
        Unit unit = unitMapper.toEntity(dto);

        // Set course relationship if courseId is provided
        if (dto.getCourseId() != null) {
            Course course = courseRepository.findById(dto.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
            unit.setCourse(course);
        }

        Unit saved = unitRepository.save(unit);
        return unitMapper.toDto(saved);
    }

    @Override
    public void deleteUnit(UUID id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found"));
        unitRepository.delete(unit);
    }
}
