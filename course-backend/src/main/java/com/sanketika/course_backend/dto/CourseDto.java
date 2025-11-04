package com.sanketika.course_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CourseDto {
    private UUID id;

    @NotBlank(message = "Course name cannot be empty or null")
    private String name;

    @NotBlank(message = "Description cannot be empty or null")
    private String description;

    @NotBlank(message = "Board cannot be empty or null")
    private String board;

//    @NotBlank(message = "Medium list cannot be empty or null")
    private List<String> medium;

//    @NotBlank(message = "Grade list cannot be empty or null")
    private List<String> grade;

//    @NotBlank(message = "Subject list cannot be empty or null")
    private List<String> subject;

    private List<UnitDto> units;

    public CourseDto() {}
}
