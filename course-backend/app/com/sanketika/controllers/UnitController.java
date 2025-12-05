package com.sanketika.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanketika.dto.UnitDto;
import com.sanketika.mapper.ResponseMapper;
import com.sanketika.services.UnitService;
import com.sanketika.utils.ApiEnvelope;
import jakarta.inject.Inject;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.util.List;
import java.util.UUID;

public class UnitController extends Controller {

    private final UnitService unitService;
    private final ObjectMapper objectMapper;

    @Inject
    public UnitController(UnitService unitService) {
        this.unitService = unitService;
        this.objectMapper = new ObjectMapper();
    }

    public Result getAllUnits(Http.Request request) {
        try {
            List<UnitDto> units = unitService.getAllUnits();
            ApiEnvelope<List<UnitDto>> response = ResponseMapper.success(
                    "api.unit.list",
                    "Units fetched successfully",
                    units
            );
            return ok(Json.toJson(response));
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError("Error fetching units: " + e.getMessage());
        }
    }

    public Result getUnitsByCourse(Http.Request request, UUID courseId) {
        try {
            List<UnitDto> units = unitService.getUnitsByCourse(courseId);
            ApiEnvelope<List<UnitDto>> response = ResponseMapper.success(
                    "api.unit.list",
                    "Units fetched successfully",
                    units
            );
            return ok(Json.toJson(response));
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError("Error fetching units: " + e.getMessage());
        }
    }

    public Result getUnitById(Http.Request request, UUID id) {
        try {
            UnitDto unit = unitService.getUnitById(id);
            ApiEnvelope<UnitDto> response = ResponseMapper.success(
                    "api.unit.get",
                    "Unit fetched successfully",
                    unit
            );
            return ok(Json.toJson(response));
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError("Error fetching unit: " + e.getMessage());
        }
    }

    public Result createUnit(Http.Request request) {
        try {
            JsonNode json = request.body().asJson();
            UnitDto dto = objectMapper.treeToValue(json, UnitDto.class);

            UnitDto created = unitService.createUnit(dto);
            ApiEnvelope<UnitDto> response = ResponseMapper.success(
                    "api.unit.create",
                    "Unit created successfully",
                    created
            );
            return ok(Json.toJson(response));
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError("Error creating unit: " + e.getMessage());
        }
    }

    public Result updateUnit(Http.Request request, UUID id) {
        try {
            JsonNode json = request.body().asJson();
            UnitDto dto = objectMapper.treeToValue(json, UnitDto.class);

            UnitDto updated = unitService.updateUnit(id, dto);
            ApiEnvelope<UnitDto> response = ResponseMapper.success(
                    "api.unit.update",
                    "Unit updated successfully",
                    updated
            );
            return ok(Json.toJson(response));
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError("Error updating unit: " + e.getMessage());
        }
    }

    public Result deleteUnit(Http.Request request, UUID id) {
        try {
            unitService.deleteUnit(id);
            ApiEnvelope<Void> response = ResponseMapper.success(
                    "api.unit.delete",
                    "Unit deleted successfully",
                    null
            );
            return ok(Json.toJson(response));
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError("Error deleting unit: " + e.getMessage());
        }
    }
}
