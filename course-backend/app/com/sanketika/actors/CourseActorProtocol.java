package com.sanketika.actors;

import com.sanketika.dto.CourseDto;
import com.sanketika.dto.CourseListRequest;

import java.util.UUID;

public class CourseActorProtocol {

    public static class ListCourses {
        public final CourseListRequest request;

        public ListCourses(CourseListRequest request) {
            this.request = request;
        }
    }

    public static class GetCourseById {
        public final UUID id;

        public GetCourseById(UUID id) {
            this.id = id;
        }
    }

    public static class CreateCourse {
        public final CourseDto courseDto;

        public CreateCourse(CourseDto courseDto) {
            this.courseDto = courseDto;
        }
    }

    public static class UpdateCourse {
        public final UUID id;
        public final CourseDto courseDto;

        public UpdateCourse(UUID id, CourseDto courseDto) {
            this.id = id;
            this.courseDto = courseDto;
        }
    }

    public static class DeleteCourse {
        public final UUID id;

        public DeleteCourse(UUID id) {
            this.id = id;
        }
    }
}
