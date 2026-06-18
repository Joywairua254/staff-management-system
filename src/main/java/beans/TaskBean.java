package beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import Service.TaskService;
import Service.StaffService;
import model.Task;
import model.Staff;
import util.DBConnection;
import jakarta.servlet.http.Part;
import java.io.InputStream;
import java.util.Base64;

@Named("taskBean")
@SessionScoped
public class TaskBean implements Serializable {

    private List<Task> allTasks;
    private List<Staff> staffList;
    private Task newTask;
    private String assignmentType; // "Individual", "All", "Group"
    private int selectedStaffId;
    private List<Integer> selectedStaffIds;
    private Part taskFile;

    private TaskService taskService;
    private StaffService staffService;

    @PostConstruct
    public void init() {
        resetNewTask();
        loadData();
    }

    public void loadData() {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            taskService = new TaskService(conn);
            staffService = new StaffService(conn);
            loadAllTasks();
            loadStaffList();
        } else {
            allTasks = new ArrayList<>();
            staffList = new ArrayList<>();
        }
    }

    public void loadAllTasks() {
        try {
            if (taskService != null) {
                allTasks = taskService.listAllTasks();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            allTasks = new ArrayList<>();
        }
    }

    public void loadStaffList() {
        try {
            if (staffService != null) {
                staffList = staffService.listStaff();
            }
        } catch (Exception e) {
            e.printStackTrace();
            staffList = new ArrayList<>();
        }
    }

    private void resetNewTask() {
        newTask = new Task();
        newTask.setDeadline(LocalDateTime.now().plusDays(1));
        newTask.setCompleted(false);
        assignmentType = "All";
        selectedStaffId = 0;
        selectedStaffIds = new ArrayList<>();
    }

    public void saveTask() {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Database Error", "Could not connect to database."));
                return;
            }

            taskService = new TaskService(conn);
            List<Integer> resolvedStaffIds = new ArrayList<>();

            if ("Individual".equals(assignmentType)) {
                if (selectedStaffId <= 0) {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Please select a staff member."));
                    return;
                }
                resolvedStaffIds.add(selectedStaffId);
            } else if ("All".equals(assignmentType)) {
                if (staffList == null || staffList.isEmpty()) {
                    loadStaffList();
                }
                for (Staff s : staffList) {
                    resolvedStaffIds.add(s.getId());
                }
            } else if ("Group".equals(assignmentType)) {
                if (selectedStaffIds == null || selectedStaffIds.isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "Please select at least one staff member."));
                    return;
                }
                resolvedStaffIds.addAll(selectedStaffIds);
            }

            taskService.createTask(newTask, resolvedStaffIds);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Task assigned successfully!"));

            resetNewTask();
            loadAllTasks();
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error saving task", e.getMessage()));
        }
    }

    public void toggleTaskCompletion(int taskId, boolean currentStatus) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                taskService = new TaskService(conn);
                taskService.changeTaskStatus(taskId, !currentStatus);
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Task status updated."));
                loadAllTasks();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error updating task status", e.getMessage()));
        }
    }

    public void deleteTask(int taskId) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                taskService = new TaskService(conn);
                taskService.removeTask(taskId);
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Task deleted successfully."));
                loadAllTasks();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error deleting task", e.getMessage()));
        }
    }

    public String getAssignedStaffNames(int taskId) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                return new TaskService(conn).getAssignedStaffNames(taskId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    public List<Task> getUserTasks(int staffId) {
        List<Task> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                list = new TaskService(conn).getTasksByStaff(staffId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Task> getActiveUserTasks(int staffId) {
        List<Task> activeTasks = new ArrayList<>();
        try {
            for (Task t : getUserTasks(staffId)) {
                if (!t.isCompleted() && !isDeadlineReached(t)) {
                    activeTasks.add(t);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activeTasks;
    }

    public boolean isDeadlineReached(Task task) {
        if (task == null || task.getDeadline() == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(task.getDeadline());
    }

    // Getters and Setters
    public List<Task> getAllTasks() {
        loadAllTasks();
        return allTasks;
    }

    public List<Staff> getStaffList() {
        loadStaffList();
        return staffList;
    }

    public Task getNewTask() {
        return newTask;
    }

    public void setNewTask(Task newTask) {
        this.newTask = newTask;
    }

    public String getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(String assignmentType) {
        this.assignmentType = assignmentType;
    }

    public int getSelectedStaffId() {
        return selectedStaffId;
    }

    public void setSelectedStaffId(int selectedStaffId) {
        this.selectedStaffId = selectedStaffId;
    }

    public List<Integer> getSelectedStaffIds() {
        return selectedStaffIds;
    }

    public void setSelectedStaffIds(List<Integer> selectedStaffIds) {
        this.selectedStaffIds = selectedStaffIds;
    }

    public Part getTaskFile() {
        return taskFile;
    }

    public void setTaskFile(Part taskFile) {
        this.taskFile = taskFile;
    }

    public void uploadTaskDoc(int taskId) {
        try {
            if (taskFile != null) {
                try (InputStream input = taskFile.getInputStream()) {
                    byte[] bytes = input.readAllBytes();
                    if (bytes.length > 0) {
                        String base64 = Base64.getEncoder().encodeToString(bytes);
                        String filename = taskFile.getSubmittedFileName();
                        Connection conn = DBConnection.getConnection();
                        if (conn != null) {
                            TaskService ts = new TaskService(conn);
                            ts.submitTaskDocument(taskId, base64, filename);
                            FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Task document uploaded successfully!"));
                            loadAllTasks();
                        } else {
                            FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Database connection error."));
                        }
                    } else {
                        FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "Selected file is empty."));
                    }
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No file selected."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Upload failed", e.getMessage()));
        }
    }
}
