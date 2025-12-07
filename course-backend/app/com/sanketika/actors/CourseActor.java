package com.sanketika.actors;

import akka.actor.AbstractActor;
import akka.actor.Status;
import com.sanketika.dto.CourseDto;
import com.sanketika.dto.PageResponse;
import com.sanketika.services.CourseService;
import jakarta.inject.Inject;

public class CourseActor extends AbstractActor {

    private final CourseService courseService;

    @Inject
    public CourseActor(CourseService courseService) {
        this.courseService = courseService;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(CourseActorProtocol.ListCourses.class, this::handleListCourses)
                .match(CourseActorProtocol.GetCourseById.class, this::handleGetCourseById)
                .match(CourseActorProtocol.CreateCourse.class, this::handleCreateCourse)
                .match(CourseActorProtocol.UpdateCourse.class, this::handleUpdateCourse)
                .match(CourseActorProtocol.DeleteCourse.class, this::handleDeleteCourse)
                .build();
    }

    private void handleListCourses(CourseActorProtocol.ListCourses msg) {
        try {
            PageResponse<CourseDto> response = courseService.listCourses(msg.request);
            sender().tell(response, self());
        } catch (Exception e) {
            sender().tell(new Status.Failure(e), self());
        }
    }

    private void handleGetCourseById(CourseActorProtocol.GetCourseById msg) {
        try {
            Object response = courseService.getCourseById(msg.id);
            sender().tell(response, self());
        } catch (Exception e) {
            sender().tell(new Status.Failure(e), self());
        }
    }

    private void handleCreateCourse(CourseActorProtocol.CreateCourse msg) {
        try {
            CourseDto response = courseService.createCourse(msg.courseDto);
            sender().tell(response, self());
        } catch (Exception e) {
            sender().tell(new Status.Failure(e), self());
        }
    }

    private void handleUpdateCourse(CourseActorProtocol.UpdateCourse msg) {
        try {
            CourseDto response = courseService.updateCourse(msg.id, msg.courseDto);
            sender().tell(response, self());
        } catch (Exception e) {
            sender().tell(new Status.Failure(e), self());
        }
    }

    private void handleDeleteCourse(CourseActorProtocol.DeleteCourse msg) {
        try {
            courseService.deleteCourse(msg.id);
            sender().tell("Course deleted successfully", self());
        } catch (Exception e) {
            sender().tell(new Status.Failure(e), self());
        }
    }
}
