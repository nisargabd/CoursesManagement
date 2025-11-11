package com.sanketika.course_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class UnitDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    @NotBlank(message = "Title is required")
    private String title;
    private String content;
    private UUID courseId;

    public UnitDto(){

    }

    public UnitDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
