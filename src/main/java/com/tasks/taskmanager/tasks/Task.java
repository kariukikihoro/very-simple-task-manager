package com.tasks.taskmanager.tasks;

import com.tasks.taskmanager.users.User;
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
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.CREATED;

    @ManyToOne
    private User assignedEmployee;

    @ManyToOne
    private User createdBy;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    public enum TaskStatus {
        CREATED, ASSIGNED, IN_PROGRESS, RESOLVED, DONE
    }
}
