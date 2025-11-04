package com.sanketika.course_backend.dto;

import lombok.Data;
import java.util.List;

/**
 * DTO for filter options
 */
@Data
public class FilterOptionsDto {
    private List<String> boards;
    private List<String> mediums;
    private List<String> grades;
    private List<String> subjects;

    // Default constructor
    public FilterOptionsDto() {
    }

    // Constructor with parameters
    public FilterOptionsDto(List<String> boards, List<String> mediums, List<String> grades, List<String> subjects) {
        this.boards = boards;
        this.mediums = mediums;
        this.grades = grades;
        this.subjects = subjects;
    }
}