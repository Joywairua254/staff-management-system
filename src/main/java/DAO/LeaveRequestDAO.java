package DAO;

import model.LeaveRequest;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LeaveRequestDAO {
    private Connection conn;

    public LeaveRequestDAO(Connection conn) {
        this.conn = conn;
    }

    public void addLeaveRequest(LeaveRequest req) throws SQLException {
        String sql = "INSERT INTO leave_request (staff_id, start_date, end_date, leave_type, reason, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, req.getStaffId());
            stmt.setDate(2, Date.valueOf(req.getStartDate()));
            stmt.setDate(3, Date.valueOf(req.getEndDate()));
            stmt.setString(4, req.getLeaveType());
            stmt.setString(5, req.getReason());
            stmt.setString(6, req.getStatus());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    req.setLeaveId(rs.getInt(1));
                }
            }
        }
    }

    public List<LeaveRequest> getLeaveRequestsByStaff(int staffId) throws SQLException {
        List<LeaveRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM leave_request WHERE staff_id = ? ORDER BY created_at DESC";
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

    public List<LeaveRequest> getAllLeaveRequests() throws SQLException {
        List<LeaveRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM leave_request ORDER BY created_at DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public void updateLeaveStatus(int leaveId, String status) throws SQLException {
        String sql = "UPDATE leave_request SET status = ? WHERE leave_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, leaveId);
            stmt.executeUpdate();
        }
    }

    private LeaveRequest mapRow(ResultSet rs) throws SQLException {
        Date startDVal = rs.getDate("start_date");
        LocalDate startD = startDVal != null ? startDVal.toLocalDate() : null;
        Date endDVal = rs.getDate("end_date");
        LocalDate endD = endDVal != null ? endDVal.toLocalDate() : null;
        Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime created = ts != null ? ts.toLocalDateTime() : null;

        return new LeaveRequest(
            rs.getInt("leave_id"),
            rs.getInt("staff_id"),
            startD,
            endD,
            rs.getString("leave_type"),
            rs.getString("reason"),
            rs.getString("status"),
            created
        );
    }
}
