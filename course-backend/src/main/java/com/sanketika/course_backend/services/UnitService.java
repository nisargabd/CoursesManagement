package com.sanketika.course_backend.services;

import com.sanketika.course_backend.dto.UnitDto;
import java.util.List;
import java.util.UUID;

public interface UnitService {
    List<UnitDto> getAllUnits();
    List<UnitDto> getUnitsByCourse(UUID id);
    UnitDto getUnitById(UUID id);
    void deleteUnit(UUID id);
    UnitDto updateUnit(UUID id, UnitDto dto);
    UnitDto createUnit(UnitDto dto);
}
