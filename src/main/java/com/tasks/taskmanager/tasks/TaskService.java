package com.tasks.taskmanager.tasks;

import com.tasks.taskmanager.files.FileStorageService;
import com.tasks.taskmanager.taskevents.TaskEvent;
import com.tasks.taskmanager.taskevents.TaskEventRepository;
import com.tasks.taskmanager.tasksfiles.TaskFile;
import com.tasks.taskmanager.tasksfiles.TaskFileRepository;
import com.tasks.taskmanager.users.User;
import com.tasks.taskmanager.users.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepo;
    private final TaskEventRepository eventRepo;
    private final TaskFileRepository fileRepo;
    private final FileStorageService storageService;
    private final UserRepository userRepo;

    public TaskService(TaskRepository taskRepo, TaskEventRepository eventRepo,
                       TaskFileRepository fileRepo, FileStorageService storageService,
                       UserRepository userRepo) {

        this.taskRepo = taskRepo;
        this.eventRepo = eventRepo;
        this.fileRepo = fileRepo;
        this.storageService = storageService;
        this.userRepo = userRepo;
    }

    @Transactional
    public Task createTask(String title, String description, User createdBy) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setCreatedBy(createdBy);
        task.setStatus(Task.TaskStatus.CREATED);
        task = taskRepo.save(task);
        addEvent(task, "Task created", createdBy.getUsername());
        return task;
    }

    @Transactional
    public Task assignTask(String taskId, String employeeId, User supervisor) {
        Task task = taskRepo.findById(taskId).orElseThrow();
        User employee = userRepo.findById(employeeId).orElseThrow();
        task.setAssignedEmployee(employee);
        task.setStatus(Task.TaskStatus.ASSIGNED);
        task.setUpdatedAt(LocalDateTime.now());
        taskRepo.save(task);
        addEvent(task, "Assigned to " + employee.getUsername(), supervisor.getUsername());
        return task;
    }

    @Transactional
    public Task updateTaskStatus(String taskId, Task.TaskStatus newStatus, String performerUsername) {
        Task task = taskRepo.findById(taskId).orElseThrow();
        Task.TaskStatus oldStatus = task.getStatus();
        task.setStatus(newStatus);
        task.setUpdatedAt(LocalDateTime.now());
        taskRepo.save(task);
        addEvent(task, "Status changed from " + oldStatus + " to " + newStatus, performerUsername);
        return task;
    }

    @Transactional
    public void addFileToTask(String taskId, MultipartFile file, User uploader) throws IOException {
        Task task = taskRepo.findById(taskId).orElseThrow();
        String storedName = storageService.storeFile(file);
        TaskFile tf = new TaskFile();
        tf.setOriginalFilename(file.getOriginalFilename());
        tf.setStoredFilename(storedName);
        tf.setContentType(file.getContentType());
        tf.setSize(file.getSize());
        tf.setTask(task);
        fileRepo.save(tf);
        addEvent(task, "File uploaded: " + file.getOriginalFilename(), uploader.getUsername());
    }

    public Optional<Task> findById(String id) {
        return taskRepo.findById(id);
    }

    public List<Task> getTasksForEmployee(User employee) {
        return taskRepo.findByAssignedEmployee(employee);
    }

    public List<Task> getTasksForSupervisor(User supervisor) {
        return taskRepo.findByCreatedBy(supervisor);
    }

    private void addEvent(Task task, String description, String performedBy) {
        TaskEvent event = new TaskEvent();
        event.setTask(task);
        event.setDescription(description);
        event.setPerformedBy(performedBy);
        eventRepo.save(event);
    }
}
