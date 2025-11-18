package com.sanketika.course_backend.controllers;

import com.sanketika.course_backend.dto.FilterOptionsDto;
import com.sanketika.course_backend.dto.FilterRequestDto;
import com.sanketika.course_backend.enums.Board;
import com.sanketika.course_backend.enums.Grade;
import com.sanketika.course_backend.enums.Medium;
import com.sanketika.course_backend.enums.Subject;
import com.sanketika.course_backend.repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/filters")
public class FilterController {

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("/boards")
    public ResponseEntity<List<String>> getBoards() {
        return ResponseEntity.ok(
                courseRepository.findDistinctBoards()
        );
    }

    @PostMapping("/mediums")
    public ResponseEntity<List<String>> getMediumsByBoard(@RequestBody FilterRequestDto request) {
        return ResponseEntity.ok(
                courseRepository.findDistinctMediumByBoard(request.getBoard())
        );
    }

    @PostMapping("/grades")
    public ResponseEntity<List<String>> getGrades(@RequestBody FilterRequestDto request) {
        return ResponseEntity.ok(
                courseRepository.findDistinctGradeByBoardAndMediums(
                        request.getBoard(),
                        request.getMedium()
                )
        );
    }

    @PostMapping("/subjects")
    public ResponseEntity<List<String>> getSubjects(@RequestBody FilterRequestDto request) {
        return ResponseEntity.ok(
                courseRepository.findDistinctSubjectsByBoardMediumsAndGrades(
                        request.getBoard(),
                        request.getMedium(),
                        request.getGrade()
                )
        );
    }

  @GetMapping("/options")
public ResponseEntity<FilterOptionsDto> getFilterOptions() {

    // Load all values directly from enums
    List<String> boards = Arrays.stream(Board.values())
            .map(Board::getDisplayName)
            .toList();

    List<String> mediums = Arrays.stream(Medium.values())
            .map(Medium::getDisplayName)
            .toList();

    List<String> grades = Arrays.stream(Grade.values())
            .map(Grade::getDisplayName)
            .toList();

    List<String> subjects = Arrays.stream(Subject.values())
            .map(Subject::getDisplayName)
            .toList();

    FilterOptionsDto dto = new FilterOptionsDto(
            boards,
            mediums,
            grades,
            subjects
    );

    return ResponseEntity.ok(dto);
}


}
