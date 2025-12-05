package com.sanketika.mapper;

import com.sanketika.dto.UnitDto;
import com.sanketika.entity.Unit;
import jakarta.inject.Singleton;

@Singleton
public class UnitMapper {

    public UnitDto toDto(Unit unit) {
        if (unit == null) return null;
        UnitDto dto = new UnitDto();
        dto.setId(unit.getId());
        dto.setTitle(unit.getTitle());
        dto.setContent(unit.getContent());
        dto.setCourseId(unit.getCourse() != null ? unit.getCourse().getId() : null);
        return dto;
    }

    public Unit toEntity(UnitDto dto) {
        if (dto == null) return null;
        Unit unit = new Unit();
        unit.setTitle(dto.getTitle());
        unit.setContent(dto.getContent());
        return unit;
    }
}
