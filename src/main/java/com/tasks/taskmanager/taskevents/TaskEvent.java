package com.tasks.taskmanager.taskevents;

import com.tasks.taskmanager.tasks.Task;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table
public class TaskEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    private Task task;

    private String description;
    private String performedBy;
    private LocalDateTime timestamp = LocalDateTime.now();

}