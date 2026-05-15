package com.tasks.taskmanager.users;

import com.tasks.taskmanager.tasks.Task;
import com.tasks.taskmanager.tasks.TaskService;
import com.tasks.taskmanager.taskevents.TaskEventRepository;
import com.tasks.taskmanager.tasksfiles.TaskFileRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/supervisor")
@PreAuthorize("hasRole('SUPERVISOR')")
public class SupervisorController {

    private final TaskService taskService;
    private final UserService userService;
    private final TaskFileRepository taskFileRepository;
    private final TaskEventRepository taskEventRepository;

    public SupervisorController(TaskService taskService, UserService userService,
                                TaskFileRepository taskFileRepository,
                                TaskEventRepository taskEventRepository) {
        this.taskService = taskService;
        this.userService = userService;
        this.taskFileRepository = taskFileRepository;
        this.taskEventRepository = taskEventRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User supervisor = userService.findByUsername(principal.getName()).orElseThrow();
        model.addAttribute("tasks", taskService.getTasksForSupervisor(supervisor));
        model.addAttribute("employees", userService.findAllEmployees());
        return "supervisor/dashboard";
    }

    @GetMapping("/create-task")
    public String showCreateTaskForm(Model model) {
        model.addAttribute("employees", userService.findAllEmployees());
        return "supervisor/create-task";
    }

    @PostMapping("/create-task")
    public String createTask(@RequestParam String title,
                             @RequestParam String description,
                             @RequestParam(required = false) String employeeId,
                             @RequestParam("file") MultipartFile file,
                             Principal principal,
                             RedirectAttributes ra) {
        try {
            User supervisor = userService.findByUsername(principal.getName()).orElseThrow();
            Task task = taskService.createTask(title, description, supervisor);
            if (StringUtils.hasText(employeeId)) {
                task = taskService.assignTask(task.getId(), employeeId, supervisor);
            }
            if (!file.isEmpty()) {
                taskService.addFileToTask(task.getId(), file, supervisor);
            }
            ra.addFlashAttribute("message", "Task created successfully");
        } catch (IOException e) {
            ra.addFlashAttribute("error", "File upload failed");
        }
        return "redirect:/supervisor/dashboard";
    }

    @GetMapping("/task/{id}")
    public String viewTask(@PathVariable String id, Model model, Principal principal) {
        Task task = taskService.findById(id).orElseThrow();
        model.addAttribute("task", task);
        model.addAttribute("employees", userService.findAllEmployees());
        model.addAttribute("files", taskFileRepository.findByTaskId(id));
        model.addAttribute("events", taskEventRepository.findByTaskIdOrderByTimestampAsc(id));
        return "supervisor/view-task";
    }

    @PostMapping("/task/{id}/assign")
    public String assignTask(@PathVariable String id,
                             @RequestParam String employeeId,
                             Principal principal) {
        User supervisor = userService.findByUsername(principal.getName()).orElseThrow();
        taskService.assignTask(id, employeeId, supervisor);
        return "redirect:/supervisor/task/" + id;
    }

    @PostMapping("/task/{id}/upload")
    public String uploadFile(@PathVariable String id,
                             @RequestParam("file") MultipartFile file,
                             Principal principal,
                             RedirectAttributes ra) {
        try {
            User supervisor = userService.findByUsername(principal.getName()).orElseThrow();
            taskService.addFileToTask(id, file, supervisor);
            ra.addFlashAttribute("message", "File uploaded");
        } catch (IOException e) {
            ra.addFlashAttribute("error", "Upload failed");
        }
        return "redirect:/supervisor/task/" + id;
    }

    @PostMapping("/task/{id}/complete")
    public String confirmCompletion(@PathVariable String id, Principal principal) {
        taskService.updateTaskStatus(id, Task.TaskStatus.DONE, principal.getName());
        return "redirect:/supervisor/task/" + id;
    }

    @GetMapping("/create-employee")
    public String showCreateEmployeeForm() {
        return "supervisor/create-employee";
    }

    @PostMapping("/create-employee")
    public String createEmployee(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String fullName,
                                 RedirectAttributes ra) {
        try {
            userService.createEmployee(username, password, fullName);
            ra.addFlashAttribute("message", "Employee created");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Username already exists or error");
        }
        return "redirect:/supervisor/dashboard";
    }
}
