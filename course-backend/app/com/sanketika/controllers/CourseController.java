package com.sanketika.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanketika.actors.CourseActorProtocol;
import com.sanketika.dto.CourseDto;
import com.sanketika.dto.CourseListRequest;
import com.sanketika.dto.PageResponse;
import com.sanketika.mapper.ResponseMapper;
import com.sanketika.utils.ApiEnvelope;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import akka.actor.ActorRef;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

import static akka.pattern.Patterns.ask;

public class CourseController extends Controller {

    private final ActorRef courseActor;
    private final ObjectMapper objectMapper;
    private final Duration timeout = Duration.ofSeconds(5);

    @Inject
    public CourseController(@Named("courseActor") ActorRef courseActor) {
        this.courseActor = courseActor;
        this.objectMapper = new ObjectMapper();
    }

    private String autoId(Http.Request request) {
        String path = request.path();
        return path.replace("/", ".").substring(1);
    }

    public CompletionStage<Result> listCourses(Http.Request request) {
        try {
            JsonNode json = request.body().asJson();
            CourseListRequest requestBody = objectMapper.treeToValue(json, CourseListRequest.class);

            return ask(courseActor, new CourseActorProtocol.ListCourses(requestBody), timeout)
                    .thenApply(response -> {
                        PageResponse<CourseDto> pageResponse = (PageResponse<CourseDto>) response;
                        ApiEnvelope<PageResponse<CourseDto>> apiResponse = ResponseMapper.success(
                                autoId(request),
                                "Courses fetched successfully",
                                pageResponse
                        );
                        return ok(Json.toJson(apiResponse));
                    })
                    .exceptionally(e -> {
                        e.printStackTrace();
                        return internalServerError("Error processing request: " + e.getMessage());
                    });
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.concurrent.CompletableFuture.completedFuture(
                    internalServerError("Error processing request: " + e.getMessage())
            );
        }
    }

    public CompletionStage<Result> getCourseById(Http.Request request, UUID id) {
        return ask(courseActor, new CourseActorProtocol.GetCourseById(id), timeout)
                .thenApply(response -> {
                    CourseDto course = objectMapper.convertValue(response, CourseDto.class);
                    ApiEnvelope<CourseDto> apiResponse = ResponseMapper.success(
                            autoId(request),
                            "Course fetched successfully",
                            course
                    );
                    return ok(Json.toJson(apiResponse));
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return internalServerError("Error fetching course: " + e.getMessage());
                });
    }

    public CompletionStage<Result> createCourse(Http.Request request) {
        try {
            JsonNode json = request.body().asJson();
            CourseDto dto = objectMapper.treeToValue(json, CourseDto.class);

            return ask(courseActor, new CourseActorProtocol.CreateCourse(dto), timeout)
                    .thenApply(response -> {
                        CourseDto created = (CourseDto) response;
                        ApiEnvelope<CourseDto> apiResponse = ResponseMapper.success(
                                autoId(request),
                                "Course created successfully",
                                created
                        );
                        return ok(Json.toJson(apiResponse));
                    })
                    .exceptionally(e -> {
                        e.printStackTrace();
                        return internalServerError("Error creating course: " + e.getMessage());
                    });
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.concurrent.CompletableFuture.completedFuture(
                    internalServerError("Error creating course: " + e.getMessage())
            );
        }
    }

    public CompletionStage<Result> updateCourse(Http.Request request, UUID id) {
        try {
            JsonNode json = request.body().asJson();
            CourseDto dto = objectMapper.treeToValue(json, CourseDto.class);

            return ask(courseActor, new CourseActorProtocol.UpdateCourse(id, dto), timeout)
                    .thenApply(response -> {
                        CourseDto updated = (CourseDto) response;
                        ApiEnvelope<CourseDto> apiResponse = ResponseMapper.success(
                                autoId(request),
                                "Course updated successfully",
                                updated
                        );
                        return ok(Json.toJson(apiResponse));
                    })
                    .exceptionally(e -> {
                        e.printStackTrace();
                        return internalServerError("Error updating course: " + e.getMessage());
                    });
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.concurrent.CompletableFuture.completedFuture(
                    internalServerError("Error updating course: " + e.getMessage())
            );
        }
    }

    public CompletionStage<Result> deleteCourse(Http.Request request, UUID id) {

        return ask(courseActor, new CourseActorProtocol.DeleteCourse(id), timeout)
                .thenApply(response -> {
                    ApiEnvelope<Void> apiResponse = ResponseMapper.success(
                            autoId(request),
                            "Course deleted successfully",
                            null
                    );
                    return ok(Json.toJson(apiResponse));
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return internalServerError("Error deleting course: " + e.getMessage());
                });
    }
}
