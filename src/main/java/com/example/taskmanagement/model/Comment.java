package com.example.taskmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Text cannot be blank")
    private String text;

    @ManyToOne
    @NotNull(message = "Author cannot be null")
    private User author;

    @ManyToOne
    @NotNull(message = "Task cannot be null")
    private Task task;
}
