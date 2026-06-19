package Service;

import DAO.TaskDAO;
import model.Task;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TaskService {
    private TaskDAO taskDAO;

    public TaskService(Connection conn) {
        this.taskDAO = new TaskDAO(conn);
    }

    public void createTask(Task task, List<Integer> assignedStaffIds) throws SQLException {
        if (task.getDescription() == null || task.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Task description is required.");
        }
        if (task.getDeadline() == null) {
            throw new IllegalArgumentException("Task deadline is required.");
        }
        if (assignedStaffIds == null || assignedStaffIds.isEmpty()) {
            throw new IllegalArgumentException("The task must be assigned to at least one staff member.");
        }
        taskDAO.addTask(task, assignedStaffIds);
    }

    public List<Task> getTasksByStaff(int staffId) throws SQLException {
        return taskDAO.getTasksByStaff(staffId);
    }

    public List<Task> listAllTasks() throws SQLException {
        return taskDAO.getAllTasks();
    }

    public String getAssignedStaffNames(int taskId) throws SQLException {
        return taskDAO.getAssignedStaffNames(taskId);
    }

    public void changeTaskStatus(int taskId, boolean completed) throws SQLException {
        taskDAO.updateTaskStatus(taskId, completed);
    }

    public void removeTask(int taskId) throws SQLException {
        taskDAO.deleteTask(taskId);
    }

    public void submitTaskDocument(int taskId, String document, String docName) throws SQLException {
        taskDAO.updateTaskSubmission(taskId, document, docName);
    }

    public void updateAdminComment(int taskId, String comment) throws SQLException {
        taskDAO.updateAdminComment(taskId, comment);
    }

    public void updateStaffComment(int taskId, String comment) throws SQLException {
        taskDAO.updateStaffComment(taskId, comment);
    }
}
