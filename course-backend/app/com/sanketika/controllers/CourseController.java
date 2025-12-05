package com.sanketika.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanketika.dto.CourseDto;
import com.sanketika.dto.CourseListRequest;
import com.sanketika.dto.PageResponse;
import com.sanketika.mapper.ResponseMapper;
import com.sanketika.services.CourseService;
import com.sanketika.utils.ApiEnvelope;
import jakarta.inject.Inject;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.util.UUID;

public class CourseController extends Controller {

    private final CourseService courseService;
    private final ObjectMapper objectMapper;

    @Inject
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
        this.objectMapper = new ObjectMapper();
    }

    private String autoId(Http.Request request) {
        String path = request.path();
        return path.replace("/", ".").substring(1);
    }

    public Result listCourses(Http.Request request) {
        try {
            JsonNode json = request.body().asJson();
            CourseListRequest requestBody = objectMapper.treeToValue(json, CourseListRequest.class);

            PageResponse<CourseDto> pageResponse = courseService.listCourses(requestBody);

            ApiEnvelope<PageResponse<CourseDto>> response = ResponseMapper.success(
                    autoId(request), 
                    "Courses fetched successfully", 
                    pageResponse
            );

            return ok(Json.toJson(response));
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError("Error processing request: " + e.getMessage());
        }
    }

    public Result getCourseById(Http.Request request, UUID id) {
        try {
            Object raw = courseService.getCourseById(id);
            CourseDto course = objectMapper.convertValue(raw, CourseDto.class);

            ApiEnvelope<CourseDto> response = ResponseMapper.success(
                    autoId(request), 
                    "Course fetched successfully", 
                    course
            );

            return ok(Json.toJson(response));
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError("Error fetching course: " + e.getMessage());
        }
    }

    public Result createCourse(Http.Request request) {
        try {
            JsonNode json = request.body().asJson();
            CourseDto dto = objectMapper.treeToValue(json, CourseDto.class);

            // TODO: Check for ADMIN role from JWT in request attributes
            // For now, we assume the AuthFilter has validated the token

            CourseDto created = courseService.createCourse(dto);

            ApiEnvelope<CourseDto> response = ResponseMapper.success(
                    autoId(request), 
                    "Course created successfully", 
                    created
            );

            return ok(Json.toJson(response));
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError("Error creating course: " + e.getMessage());
        }
    }

    public Result updateCourse(Http.Request request, UUID id) {
        try {
            JsonNode json = request.body().asJson();
            CourseDto dto = objectMapper.treeToValue(json, CourseDto.class);

            // TODO: Check for ADMIN role from JWT in request attributes

            CourseDto updated = courseService.updateCourse(id, dto);

            ApiEnvelope<CourseDto> response = ResponseMapper.success(
                    autoId(request), 
                    "Course updated successfully", 
                    updated
            );

            return ok(Json.toJson(response));
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError("Error updating course: " + e.getMessage());
        }
    }

    public Result deleteCourse(Http.Request request, UUID id) {
        try {
            // TODO: Check for ADMIN role from JWT in request attributes

            courseService.deleteCourse(id);

            ApiEnvelope<Void> response = ResponseMapper.success(
                    autoId(request), 
                    "Course deleted successfully", 
                    null
            );

            return ok(Json.toJson(response));
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError("Error deleting course: " + e.getMessage());
        }
    }
}
