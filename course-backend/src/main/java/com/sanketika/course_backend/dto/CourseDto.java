package com.sanketika.course_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
public class CourseDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;

    @NotBlank(message = "Course name cannot be empty or null")
    private String name;

    @NotBlank(message = "Description cannot be empty or null")
    private String description;

    @NotBlank(message = "Board cannot be empty or null")
    private String board;

    private List<String> medium;

    private List<String> grade;

    private List<String> subject;

    private List<UnitDto> units;

    private String status;

    public CourseDto() {}
}
