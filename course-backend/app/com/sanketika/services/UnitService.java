package com.sanketika.services;

import com.google.inject.ImplementedBy;
import com.sanketika.dto.UnitDto;
import java.util.List;
import java.util.UUID;

@ImplementedBy(UnitServiceImpl.class)
public interface UnitService {
    List<UnitDto> getAllUnits();
    List<UnitDto> getUnitsByCourse(UUID id);
    UnitDto getUnitById(UUID id);
    void deleteUnit(UUID id);
    UnitDto updateUnit(UUID id, UnitDto dto);
    UnitDto createUnit(UnitDto dto);
}
