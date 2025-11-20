package com.sanketika.course_backend.services;

import com.sanketika.course_backend.dto.CourseDto;
import com.sanketika.course_backend.dto.CourseListRequest;
import com.sanketika.course_backend.dto.UnitDto;
import com.sanketika.course_backend.entity.Course;
import com.sanketika.course_backend.entity.Unit;
import com.sanketika.course_backend.exceptions.ResourceNotFoundException;
import com.sanketika.course_backend.mapper.CourseMapper;
import com.sanketika.course_backend.repositories.CourseRepository;
import com.sanketika.course_backend.repositories.UnitRepository;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;


import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {


    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private CourseMapper courseMapper;


@Override
@Cacheable(value = "courses", key = "#id")
public Object getCourseById(UUID id) {
    return courseMapper.toDto(
        courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found"))
    );
}
    @Override
    @CacheEvict(value = "courses", allEntries = true)
    public CourseDto createCourse(CourseDto dto) {
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
    @CacheEvict(value = "courses", allEntries = true)
    public CourseDto updateCourse(UUID id, CourseDto dto) {
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
    @CacheEvict(value = "courses", allEntries = true)
    public void deleteCourse(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

        course.setDeleted(true);
        courseRepository.save(course);
    }

    @Override
    public Page<CourseDto> listCourses(CourseListRequest request) {
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(a -> a.equals("ROLE_ADMIN"));

        List<String> allowedStatuses = new ArrayList<>();
        allowedStatuses.add("live");
        if (isAdmin) {
            allowedStatuses.add("draft");
        }

        Specification<Course> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isFalse(root.get("deleted")));

            predicates.add(root.get("status").in(allowedStatuses));

            if (request.getSearchText() != null && !request.getSearchText().isBlank()) {
                String likePattern = "%" + request.getSearchText().toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), likePattern);
                Predicate descLike = cb.like(cb.lower(root.get("description")), likePattern);
                predicates.add(cb.or(nameLike, descLike));
            }

            if (request.getBoards() != null && !request.getBoards().isEmpty()) {
                predicates.add(root.get("board").in(request.getBoards()));
            }

if (request.getMediums() != null && !request.getMediums().isEmpty()) {
    List<Predicate> mediumPreds = new ArrayList<>();
    for (String m : request.getMediums()) {
        if (m != null && !m.isBlank()) {
            mediumPreds.add(cb.like(cb.lower(root.get("medium")), "%" + m.toLowerCase() + "%"));
        }
    }
    if (!mediumPreds.isEmpty()) {
        predicates.add(cb.or(mediumPreds.toArray(new Predicate[0])));
    }
}

if (request.getGrades() != null && !request.getGrades().isEmpty()) {
    List<Predicate> gradePreds = new ArrayList<>();
    for (String g : request.getGrades()) {
        if (g != null && !g.isBlank()) {
            gradePreds.add(cb.like(cb.lower(root.get("grade")), "%" + g.toLowerCase() + "%"));
        }
    }
    if (!gradePreds.isEmpty()) {
        predicates.add(cb.or(gradePreds.toArray(new Predicate[0])));
    }
}

if (request.getSubjects() != null && !request.getSubjects().isEmpty()) {
    List<Predicate> subjectPreds = new ArrayList<>();
    for (String s : request.getSubjects()) {
        if (s != null && !s.isBlank()) {
            subjectPreds.add(cb.like(cb.lower(root.get("subject")), "%" + s.toLowerCase() + "%"));
        }
    }
    if (!subjectPreds.isEmpty()) {
        predicates.add(cb.or(subjectPreds.toArray(new Predicate[0])));
    }
}

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        int page = Math.max(0, request.getPage());
        int size = Math.max(1, request.getSize());
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Course> coursePage = courseRepository.findAll(spec, pageable);

        List<CourseDto> dtoList = coursePage.getContent().stream()
                .map(course -> {
                    UUID id = course.getId();
                    Cache cache = cacheManager.getCache("courses");
                    if (cache != null) {
                        Cache.ValueWrapper wrapper = cache.get(id);
                        if (wrapper != null && wrapper.get() instanceof CourseDto) {
                            return (CourseDto) wrapper.get();
                        }
                    }
                    CourseDto dto = courseMapper.toDto(course);
                    if (cache != null) {
                        cache.put(id, dto);
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, coursePage.getTotalElements());
    }
}
