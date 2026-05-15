package com.tasks.taskmanager.taskevents;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskEventRepository extends JpaRepository<TaskEvent, String> {
    List<TaskEvent> findByTaskIdOrderByTimestampAsc(String taskId);
}
