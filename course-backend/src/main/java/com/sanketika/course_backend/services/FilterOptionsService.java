package com.sanketika.course_backend.services;

import com.sanketika.course_backend.dto.FilterOptionsDto;
import com.sanketika.course_backend.enums.Board;
import com.sanketika.course_backend.enums.Grade;
import com.sanketika.course_backend.enums.Medium;
import com.sanketika.course_backend.enums.Subject;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for providing filter options
 */
@Service
public class FilterOptionsService {

    /**
     * Get all available filter options
     * @return FilterOptionsDto containing all filter options
     */
    public FilterOptionsDto getFilterOptions() {
        List<String> boards = Arrays.stream(Board.values())
                .map(Board::getDisplayName)
                .collect(Collectors.toList());

        List<String> mediums = Arrays.stream(Medium.values())
                .map(Medium::getDisplayName)
                .collect(Collectors.toList());

        List<String> grades = Arrays.stream(Grade.values())
                .map(Grade::getDisplayName)
                .collect(Collectors.toList());

        List<String> subjects = Arrays.stream(Subject.values())
                .map(Subject::getDisplayName)
                .collect(Collectors.toList());

        return new FilterOptionsDto(boards, mediums, grades, subjects);
    }
}
