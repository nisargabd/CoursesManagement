package com.sanketika.course_backend.mapper;

import com.sanketika.course_backend.dto.CourseDto;
import com.sanketika.course_backend.dto.UnitDto;
import com.sanketika.course_backend.entity.Course;
import com.sanketika.course_backend.entity.Unit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseMapper {

    // Convert Entity → DTO
    public CourseDto toDto(Course course) {
        if (course == null) return null;
        CourseDto dto = new CourseDto();
        dto.setId(course.getId());
        dto.setName(course.getName());
        dto.setDescription(course.getDescription());
        dto.setBoard(course.getBoard());
        dto.setMedium(course.getMedium());
        dto.setGrade(course.getGrade());
        dto.setSubject(course.getSubject());

        if (course.getUnits() != null) {
            dto.setUnits(course.getUnits().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    // Convert DTO → Entity
    public Course toEntity(CourseDto dto) {
        if (dto == null) return null;
        Course course = new Course();
        course.setId(dto.getId());
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setBoard(dto.getBoard());
        course.setMedium(dto.getMedium());
        course.setGrade(dto.getGrade());
        course.setSubject(dto.getSubject());

        if (dto.getUnits() != null) {
            List<Unit> units = dto.getUnits().stream()
                    .map(u -> toEntity(u, course)) // pass parent reference
                    .collect(Collectors.toList());
            course.setUnits(units);
        }

        return course;
    }

    // Unit → DTO
    public UnitDto toDto(Unit unit) {
        if (unit == null) return null;
        UnitDto dto = new UnitDto();
        dto.setId(unit.getId());
        dto.setTitle(unit.getTitle());
        dto.setContent(unit.getContent());
        dto.setCourseId(unit.getCourse() != null ? unit.getCourse().getId() : null);
        return dto;
    }

    // Unit DTO → Entity
    public Unit toEntity(UnitDto dto, Course course) {
        if (dto == null) return null;
        Unit unit = new Unit();
        unit.setId(dto.getId());
        unit.setTitle(dto.getTitle());
        unit.setContent(dto.getContent());
        unit.setCourse(course); // ✅ fixes courseId = null
        return unit;
    }
}
