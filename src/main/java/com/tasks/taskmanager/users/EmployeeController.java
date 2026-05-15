package com.tasks.taskmanager.users;

import com.tasks.taskmanager.tasks.Task;
import com.tasks.taskmanager.tasks.TaskService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/employee")
@PreAuthorize("hasRole('EMPLOYEE')")
public class EmployeeController {

    private final TaskService taskService;
    private final UserService userService;

    public EmployeeController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User employee = userService.findByUsername(principal.getName()).orElseThrow();
        model.addAttribute("tasks", taskService.getTasksForEmployee(employee));
        return "employee/dashboard";
    }

    @GetMapping("/task/{id}")
    public String viewTask(@PathVariable String id, Model model, Principal principal) {
        Task task = taskService.findById(id).orElseThrow();
        model.addAttribute("task", task);
        return "employee/view-task";
    }

    @PostMapping("/task/{id}/update-status")
    public String updateStatus(@PathVariable String id,
                               @RequestParam String newStatus,
                               Principal principal) {
        Task.TaskStatus status = Task.TaskStatus.valueOf(newStatus.toUpperCase());
        taskService.updateTaskStatus(id, status, principal.getName());
        return "redirect:/employee/task/" + id;
    }
}
