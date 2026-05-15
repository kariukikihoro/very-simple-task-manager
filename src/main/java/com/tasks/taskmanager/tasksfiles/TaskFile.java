package com.tasks.taskmanager.tasksfiles;

import com.tasks.taskmanager.tasks.Task;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table
public class TaskFile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String originalFilename;
    private String storedFilename;
    private String contentType;
    private long size;

    @ManyToOne
    private Task task;
}