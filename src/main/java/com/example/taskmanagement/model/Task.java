package com.example.taskmanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Title cannot be blank")
    @Size(max = 100, message = "Title should not exceed 100 characters")
    private String title;

    @Column(nullable = true)
    @Size(max = 500, message = "Description should not exceed 500 characters")
    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status cannot be null")
    private Status status;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Priority cannot be null")
    private Priority priority;

    @ManyToOne
    @NotNull(message = "Author cannot be null")
    private User author;

    @ManyToOne
    private User assignee;
}
