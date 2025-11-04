package com.sanketika.course_backend.controllers;

import com.sanketika.course_backend.dto.UnitDto;
import com.sanketika.course_backend.mapper.ResponseMapper;
import com.sanketika.course_backend.services.UnitService;
import com.sanketika.course_backend.utils.ApiEnvelope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/units")
@CrossOrigin(origins = {"http://localhost:4800", "http://localhost:4200"})
public class UnitController {

    @Autowired
    private UnitService unitService;

    @GetMapping
    public ResponseEntity<ApiEnvelope<List<UnitDto>>> getAllUnits() {
        List<UnitDto> units = unitService.getAllUnits();
        ApiEnvelope<List<UnitDto>> response = ResponseMapper.success(
                "api.unit.list",
                "Units fetched successfully",
                units
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/course/get/{courseId}")
    public ResponseEntity<ApiEnvelope<List<UnitDto>>> getUnitsByCourse(@PathVariable UUID courseId) {
        List<UnitDto> units = unitService.getUnitsByCourse(courseId);
        ApiEnvelope<List<UnitDto>> response = ResponseMapper.success(
                "api.unit.list",
                "Units fetched successfully",
                units
        );
        return ResponseEntity.ok(response);
    }


    @GetMapping("/get/{id}")
    public ResponseEntity<ApiEnvelope<UnitDto>> getUnitById(@PathVariable UUID id) {
        UnitDto unit = unitService.getUnitById(id);
        ApiEnvelope<UnitDto> response = ResponseMapper.success(
                "api.unit.get",
                "Unit fetched successfully",
                unit
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<ApiEnvelope<UnitDto>> createUnit(@RequestBody UnitDto dto){
        UnitDto created= unitService.createUnit(dto);
        ApiEnvelope<UnitDto> response = ResponseMapper.success(
                "api.unit.create",
                "Unit created successfully",
                created
        );
        return ResponseEntity.ok(response);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<ApiEnvelope<UnitDto>> updateUnit(@PathVariable UUID id, @RequestBody UnitDto dto) {
        UnitDto updated = unitService.updateUnit(id, dto);
        ApiEnvelope<UnitDto> response = ResponseMapper.success(
                "api.unit.update",
                "Unit updated successfully",
                updated
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiEnvelope<Void>> deleteUnit(@PathVariable UUID id) {
        unitService.deleteUnit(id);
        ApiEnvelope<Void> response = ResponseMapper.success(
                "api.unit.delete",
                "Unit deleted successfully",
                null
        );
        return ResponseEntity.ok(response);
    }
}
