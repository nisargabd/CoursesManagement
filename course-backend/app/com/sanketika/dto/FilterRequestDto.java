package com.sanketika.dto;

import lombok.Data;
import java.util.List;

@Data
public class FilterRequestDto {
    private String board;
    private List<String> medium;
    private List<String> grade;
}
