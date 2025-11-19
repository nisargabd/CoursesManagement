package com.sanketika.course_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class CourseListRequest {
    private int page = 0;
    private int size = 10;
    private String searchText;
    private List<String> boards;
    private List<String> mediums;
    private List<String> grades;
    private List<String> subjects;
}
