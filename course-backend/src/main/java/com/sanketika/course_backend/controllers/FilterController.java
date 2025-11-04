package com.sanketika.course_backend.controllers;

import com.sanketika.course_backend.dto.FilterOptionsDto;
import com.sanketika.course_backend.dto.FilterRequestDto;
import com.sanketika.course_backend.services.CourseServiceImpl;
import com.sanketika.course_backend.services.FilterOptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for filter options
 */
@RestController
@CrossOrigin(origins = {"http://localhost:4800", "http://localhost:4200"})
@RequestMapping("/api/filters")
public class FilterController {

    @Autowired
    private FilterOptionsService filterOptionsService;

    @Autowired
    private CourseServiceImpl courseService;

    @PostMapping("/mediums")
    public ResponseEntity<List<String>> getMediumsByBoard(@RequestBody FilterRequestDto request) {
        return ResponseEntity.ok(courseService.getMediumsByBoard(request.getBoard()));
    }

    @PostMapping("/grades")
    public ResponseEntity<List<String>> getGradesByBoardAndMedium(@RequestBody FilterRequestDto request) {
        return ResponseEntity.ok(courseService.getGradesByBoardAndMedium(request.getBoard(), request.getMedium()));
    }

     @PostMapping("/subjects")
    public ResponseEntity<List<String>> getSubjectsByBoardMediumAndGrade(@RequestBody FilterRequestDto request) {
        return ResponseEntity.ok(courseService.getSubjectsByBoardMediumAndGrade(
                request.getBoard(),
                request.getMedium(),
                request.getGrade()
        ));
    }

   @GetMapping("/boards")
   public ResponseEntity<List<String>> getBoards() {
       return ResponseEntity.ok(courseService.getAllBoards());
   }
//
//    @GetMapping("/mediums")
//    public ResponseEntity<List<String>> getMediumsByBoard(@RequestBody String board) {
//        return ResponseEntity.ok(courseService.getMediumsByBoard(board));
//    }
//
//    @GetMapping("/grades")
//    public ResponseEntity<List<String>> getGradesByBoardAndMedium(
//            @RequestBody String board,
//            @RequestBody String medium) {
//        return ResponseEntity.ok(courseService.getGradesByBoardAndMedium(board, medium));
//    }
//
//    @GetMapping("/subjects")
//    public ResponseEntity<List<String>> getSubjectsByBoardMediumAndGrade(
//            @RequestBody String board,
//            @RequestBody String medium,
//            @RequestBody String grade) {
//        return ResponseEntity.ok(courseService.getSubjectsByBoardMediumAndGrade(board, medium, grade));
//    }

    /**
     * Get all available filter options
     * @return FilterOptionsDto containing boards, mediums, grades, and subjects
     */
    @GetMapping("/options")
    public ResponseEntity<FilterOptionsDto> getFilterOptions() {
        FilterOptionsDto options = filterOptionsService.getFilterOptions();
        return ResponseEntity.ok(options);
    }
}