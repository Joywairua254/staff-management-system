package DAO;

import model.Task;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    private Connection conn;

    public TaskDAO(Connection conn) {
        this.conn = conn;
    }

    public void addTask(Task task, List<Integer> assignedStaffIds) throws SQLException {
        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);
            
            // 1. Insert the Task
            String taskSql = "INSERT INTO task (description, deadline, completed) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(taskSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, task.getDescription());
                stmt.setTimestamp(2, task.getDeadline() != null ? Timestamp.valueOf(task.getDeadline()) : null);
                stmt.setBoolean(3, task.isCompleted());
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        task.setTaskId(rs.getInt(1));
                    }
                }
            }

            // 2. Insert assignments in batch
            int taskId = task.getTaskId();
            if (taskId > 0 && assignedStaffIds != null && !assignedStaffIds.isEmpty()) {
                String assignSql = "INSERT INTO task_assignment (task_id, staff_id) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(assignSql)) {
                    for (int staffId : assignedStaffIds) {
                        stmt.setInt(1, taskId);
                        stmt.setInt(2, staffId);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    public List<Task> getTasksByStaff(int staffId) throws SQLException {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT t.* FROM task t JOIN task_assignment ta ON t.task_id = ta.task_id WHERE ta.staff_id = ? ORDER BY t.created_at DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, staffId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public List<Task> getAllTasks() throws SQLException {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM task ORDER BY created_at DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public String getAssignedStaffNames(int taskId) throws SQLException {
        // Find total count of staff to see if it's assigned to "All"
        int totalStaff = 0;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM staff")) {
            if (rs.next()) {
                totalStaff = rs.getInt(1);
            }
        }

        List<String> names = new ArrayList<>();
        String sql = "SELECT s.F_name, s.L_Name FROM staff s JOIN task_assignment ta ON s.staff_id = ta.staff_id WHERE ta.task_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, taskId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    names.add(rs.getString("F_name") + " " + rs.getString("L_Name"));
                }
            }
        }

        if (names.size() == totalStaff && totalStaff > 0) {
            return "All Staff";
        }
        return String.join(", ", names);
    }

    public void updateTaskStatus(int taskId, boolean completed) throws SQLException {
        String sql = "UPDATE task SET completed = ? WHERE task_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, completed);
            stmt.setInt(2, taskId);
            stmt.executeUpdate();
        }
    }

    public void deleteTask(int taskId) throws SQLException {
        // Due to CASCADE foreign keys, deleting a task deletes its task_assignments automatically
        String sql = "DELETE FROM task WHERE task_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, taskId);
            stmt.executeUpdate();
        }
    }

    private Task mapRow(ResultSet rs) throws SQLException {
        Timestamp dlTs = rs.getTimestamp("deadline");
        LocalDateTime dl = dlTs != null ? dlTs.toLocalDateTime() : null;
        Timestamp crTs = rs.getTimestamp("created_at");
        LocalDateTime created = crTs != null ? crTs.toLocalDateTime() : null;

        return new Task(
            rs.getInt("task_id"),
            rs.getString("description"),
            dl,
            rs.getBoolean("completed"),
            created,
            rs.getString("document"),
            rs.getString("doc_name")
        );
    }

    public void updateTaskSubmission(int taskId, String document, String docName) throws SQLException {
        String sql = "UPDATE task SET document = ?, doc_name = ? WHERE task_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, document);
            stmt.setString(2, docName);
            stmt.setInt(3, taskId);
            stmt.executeUpdate();
        }
    }
}
