package com.tasks.taskmanager.files;

import com.tasks.taskmanager.tasksfiles.TaskFile;
import com.tasks.taskmanager.tasksfiles.TaskFileRepository;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.MalformedURLException;

@Controller
public class FileController {

    private final TaskFileRepository fileRepo;
    private final FileStorageService storageService;

    public FileController(TaskFileRepository fileRepo, FileStorageService storageService) {
        this.fileRepo = fileRepo;
        this.storageService = storageService;
    }

    @GetMapping("/files/{fileId}")
    public ResponseEntity<Resource> serveFile(@PathVariable String fileId, Authentication auth) {
        TaskFile tf = fileRepo.findById(fileId).orElseThrow();
        // Basic access check: user must be the assigned employee or the supervisor (creator)
        boolean isAssigned = tf.getTask().getAssignedEmployee() != null &&
                tf.getTask().getAssignedEmployee().getUsername().equals(auth.getName());
        boolean isCreator = tf.getTask().getCreatedBy().getUsername().equals(auth.getName());
        boolean isSupervisor = auth.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals("ROLE_SUPERVISOR"));
        // Supervisors can view any file of tasks they created, employees only their own tasks.
        if (!(isAssigned || (isCreator && isSupervisor))) {
            throw new AccessDeniedException("Access denied");
        }
        try {
            Resource resource = storageService.loadFileAsResource(tf.getStoredFilename());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(tf.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + tf.getOriginalFilename() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        }
    }
}