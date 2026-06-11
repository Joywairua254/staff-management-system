package DAO;

import model.attendance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class attendanceDAO {
    private Connection conn;

    public attendanceDAO(Connection conn) {
        this.conn = conn;
    }

    // Add attendance record
    public void addAttendance(attendance att) throws SQLException {
        String sql = "INSERT INTO attendance (staff_id, check_in, check_out, attendance_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, att.getStaff_id());
            stmt.setTimestamp(2, Timestamp.valueOf(att.getCheck_in()));   // convert LocalDateTime → Timestamp
            stmt.setTimestamp(3, Timestamp.valueOf(att.getCheck_out()));  // convert LocalDateTime → Timestamp
            stmt.setDate(4, Date.valueOf(att.getAttendance_date()));      // convert LocalDate → Date

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    att.setAttendance_id(rs.getInt(1));
                }
            }
        }
    }

    // Fetch all attendance records
    public List<attendance> getAllAttendance() throws SQLException {
        List<attendance> attList = new ArrayList<>();
        String sql = "SELECT * FROM attendance";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                attendance att = new attendance(
                    rs.getInt("attendance_id"),
                    rs.getInt("staff_id"),
                    rs.getTimestamp("check_in").toLocalDateTime(),   // convert Timestamp → LocalDateTime
                    rs.getTimestamp("check_out").toLocalDateTime(),  // convert Timestamp → LocalDateTime
                    rs.getDate("attendance_date").toLocalDate()      // convert Date → LocalDate
                );
                attList.add(att);
            }
        }
        return attList;
    }
}
