package com.sanketika.course_backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sanketika.course_backend.utils.StringListConverter;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private String name;
    private String description;
    private String board;

    @Column
    @Convert(converter = StringListConverter.class)
    private List<String> medium;

    @Column
    @Convert(converter = StringListConverter.class)
    private List<String> grade;

    @Column
    @Convert(converter = StringListConverter.class)
    private List<String> subject;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "course", cascade = {CascadeType.ALL}, orphanRemoval = false, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Unit> units = new ArrayList<>();

    @Column(nullable = false)
    private String status = "live";

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    public Course(){
    }

}
