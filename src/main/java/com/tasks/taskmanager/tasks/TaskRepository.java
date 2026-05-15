package com.tasks.taskmanager.tasks;

import com.tasks.taskmanager.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByAssignedEmployee(User employee);
    List<Task> findByCreatedBy(User supervisor);
    List<Task> findByStatus(Task.TaskStatus status);
}
