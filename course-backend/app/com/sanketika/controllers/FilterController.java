package com.sanketika.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanketika.dto.FilterOptionsDto;
import com.sanketika.dto.FilterRequestDto;
import com.sanketika.enums.Board;
import com.sanketika.enums.Grade;
import com.sanketika.enums.Medium;
import com.sanketika.enums.Subject;
import com.sanketika.repositories.CourseRepository;
import jakarta.inject.Inject;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.util.Arrays;
import java.util.List;

public class FilterController extends Controller {

    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    @Inject
    public FilterController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
        this.objectMapper = new ObjectMapper();
    }

    public Result getBoards(Http.Request request) {
        try {
            List<String> boards = courseRepository.findDistinctBoards();
            return ok(Json.toJson(boards));
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError("Error fetching boards: " + e.getMessage());
        }
    }

    public Result getMediumsByBoard(Http.Request request) {
        try {
            JsonNode json = request.body().asJson();
            FilterRequestDto filterRequest = objectMapper.treeToValue(json, FilterRequestDto.class);

            List<String> mediums = courseRepository.findDistinctMediumByBoard(filterRequest.getBoard());
            return ok(Json.toJson(mediums));
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError("Error fetching mediums: " + e.getMessage());
        }
    }

    public Result getGrades(Http.Request request) {
        try {
            JsonNode json = request.body().asJson();
            FilterRequestDto filterRequest = objectMapper.treeToValue(json, FilterRequestDto.class);

            List<String> grades = courseRepository.findDistinctGradeByBoardAndMediums(
                    filterRequest.getBoard(),
                    filterRequest.getMedium()
            );
            return ok(Json.toJson(grades));
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError("Error fetching grades: " + e.getMessage());
        }
    }

    public Result getSubjects(Http.Request request) {
        try {
            JsonNode json = request.body().asJson();
            FilterRequestDto filterRequest = objectMapper.treeToValue(json, FilterRequestDto.class);

            List<String> subjects = courseRepository.findDistinctSubjectsByBoardMediumsAndGrades(
                    filterRequest.getBoard(),
                    filterRequest.getMedium(),
                    filterRequest.getGrade()
            );
            return ok(Json.toJson(subjects));
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError("Error fetching subjects: " + e.getMessage());
        }
    }

    public Result getFilterOptions(Http.Request request) {
        try {
            List<String> boards = Arrays.stream(Board.values())
                    .map(Board::getDisplayName)
                    .toList();

            List<String> mediums = Arrays.stream(Medium.values())
                    .map(Medium::getDisplayName)
                    .toList();

            List<String> grades = Arrays.stream(Grade.values())
                    .map(Grade::getDisplayName)
                    .toList();

            List<String> subjects = Arrays.stream(Subject.values())
                    .map(Subject::getDisplayName)
                    .toList();

            FilterOptionsDto dto = new FilterOptionsDto(
                    boards,
                    mediums,
                    grades,
                    subjects
            );

            return ok(Json.toJson(dto));
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError("Error fetching filter options: " + e.getMessage());
        }
    }
}
