package com.sanketika.services;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanketika.cache.RedisCache;
import com.sanketika.dto.CourseDto;
import com.sanketika.dto.CourseListRequest;
import com.sanketika.dto.PageResponse;
import com.sanketika.dto.UnitDto;
import com.sanketika.entity.Course;
import com.sanketika.entity.Unit;
import com.sanketika.exceptions.ResourceNotFoundException;
import com.sanketika.mapper.CourseMapper;
import com.sanketika.repositories.CourseRepository;
import com.sanketika.repositories.UnitRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import play.db.jpa.JPAApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UnitRepository unitRepository;
    private final CourseMapper courseMapper;
    private final RedisCache redisCache;
    private final JPAApi jpaApi;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    public CourseServiceImpl(CourseRepository courseRepository, 
                             UnitRepository unitRepository,
                             CourseMapper courseMapper,
                             RedisCache redisCache,
                             JPAApi jpaApi) {
        this.courseRepository = courseRepository;
        this.unitRepository = unitRepository;
        this.courseMapper = courseMapper;
        this.redisCache = redisCache;
        this.jpaApi = jpaApi;
    }

    @Override
    public Object getCourseById(UUID id) {
        String cacheKey = "courses:" + id.toString();
        
        // Try to get from cache
        try {
            Optional<String> cached = redisCache.get(cacheKey);
            if (cached.isPresent()) {
                return objectMapper.readValue(cached.get(), CourseDto.class);
            }
        } catch (Exception e) {
            // Cache miss or error, continue to DB
        }

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        CourseDto dto = courseMapper.toDto(course);
        
        // Cache the result
        try {
            redisCache.set(cacheKey, objectMapper.writeValueAsString(dto), 3600);
        } catch (Exception e) {
            // Ignore cache errors
        }
        
        return dto;
    }

    @Override
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
                units.add(unitRepository.save(unit));
            }
            savedCourse.setUnits(units);
        }

        // Clear cache
        clearCoursesCache();

        return courseMapper.toDto(savedCourse);
    }

    @Override
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

        // Clear cache
        clearCoursesCache();
        redisCache.del("courses:" + id.toString());

        return courseMapper.toDto(updated);
    }

    @Override
    public void deleteCourse(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

        course.setDeleted(true);
        courseRepository.save(course);

        // Clear cache
        clearCoursesCache();
        redisCache.del("courses:" + courseId.toString());
    }

    @Override
    public PageResponse<CourseDto> listCourses(CourseListRequest request) {
        return jpaApi.withTransaction(em -> {
            // Determine if user is admin (simplified - in real scenario, get from JWT in request context)
            boolean isAdmin = false; // TODO: Extract from JWT in AuthFilter
            
            List<String> allowedStatuses = new ArrayList<>();
            allowedStatuses.add("live");
            if (isAdmin) {
                allowedStatuses.add("draft");
            }

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Course> cq = cb.createQuery(Course.class);
            Root<Course> root = cq.from(Course.class);

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

            cq.where(cb.and(predicates.toArray(new Predicate[0])));
            cq.orderBy(cb.desc(root.get("createdAt")));

            TypedQuery<Course> query = em.createQuery(cq);

            int page = Math.max(0, request.getPage());
            int size = Math.max(1, request.getSize());
            query.setFirstResult(page * size);
            query.setMaxResults(size);

            List<Course> courses = query.getResultList();

            // Count total
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<Course> countRoot = countQuery.from(Course.class);
            countQuery.select(cb.count(countRoot));
            countQuery.where(cb.and(predicates.toArray(new Predicate[0])));
            long totalElements = em.createQuery(countQuery).getSingleResult();

            List<CourseDto> dtoList = courses.stream()
                    .map(course -> {
                        UUID id = course.getId();
                        String cacheKey = "courses:" + id.toString();
                        try {
                            Optional<String> cached = redisCache.get(cacheKey);
                            if (cached.isPresent()) {
                                return objectMapper.readValue(cached.get(), CourseDto.class);
                            }
                            CourseDto dto = courseMapper.toDto(course);
                            redisCache.set(cacheKey, objectMapper.writeValueAsString(dto), 3600);
                            return dto;
                        } catch (Exception ex) {
                            return courseMapper.toDto(course);
                        }
                    })
                    .collect(Collectors.toList());

            PageResponse<CourseDto> response = new PageResponse<>();
            response.setContent(dtoList);
            response.setPage(page);
            response.setSize(size);
            response.setTotalElements(totalElements);
            response.setTotalPages((int) Math.ceil((double) totalElements / size));
            response.setFirst(page == 0);
            response.setLast(page >= response.getTotalPages() - 1);
            response.setNumberOfElements(dtoList.size());

            return response;
        });
    }

    private void clearCoursesCache() {
        // In a real scenario, you'd want a more sophisticated cache invalidation strategy
        // For now, we just clear individual keys when needed
    }
}
