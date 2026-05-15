package com.tasks.taskmanager.tasksfiles;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskFileRepository extends JpaRepository<TaskFile, String> {
    List<TaskFile> findByTaskId(String taskId);
}
