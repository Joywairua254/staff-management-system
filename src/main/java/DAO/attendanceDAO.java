package DAO;

import model.attendance;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
            stmt.setTimestamp(2, att.getCheck_in() != null ? Timestamp.valueOf(att.getCheck_in()) : null);
            stmt.setTimestamp(3, att.getCheck_out() != null ? Timestamp.valueOf(att.getCheck_out()) : null);
            stmt.setDate(4, att.getAttendance_date() != null ? Date.valueOf(att.getAttendance_date()) : null);

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
                Timestamp inTs = rs.getTimestamp("check_in");
                LocalDateTime inTime = inTs != null ? inTs.toLocalDateTime() : null;
                Timestamp outTs = rs.getTimestamp("check_out");
                LocalDateTime outTime = outTs != null ? outTs.toLocalDateTime() : null;
                Date dVal = rs.getDate("attendance_date");
                LocalDate d = dVal != null ? dVal.toLocalDate() : null;

                attendance att = new attendance(
                    rs.getInt("attendance_id"),
                    rs.getInt("staff_id"),
                    inTime,
                    outTime,
                    d,
                    rs.getBoolean("verified")
                );
                attList.add(att);
            }
        }
        return attList;
    }

    public void updateAttendance(attendance att) throws SQLException {
        String sql = "UPDATE attendance SET check_in = ?, check_out = ?, attendance_date = ?, verified = ? WHERE attendance_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, att.getCheck_in() != null ? Timestamp.valueOf(att.getCheck_in()) : null);
            stmt.setTimestamp(2, att.getCheck_out() != null ? Timestamp.valueOf(att.getCheck_out()) : null);
            stmt.setDate(3, att.getAttendance_date() != null ? Date.valueOf(att.getAttendance_date()) : null);
            stmt.setBoolean(4, att.isVerified());
            stmt.setInt(5, att.getAttendance_id());
            stmt.executeUpdate();
        }
    }

    public void verifyAttendance(int attendanceId) throws SQLException {
        String sql = "UPDATE attendance SET verified = 1 WHERE attendance_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attendanceId);
            stmt.executeUpdate();
        }
    }

    public attendance getAttendanceForStaffDate(int staffId, LocalDate date) throws SQLException {
        String sql = "SELECT * FROM attendance WHERE staff_id = ? AND attendance_date = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, staffId);
            stmt.setDate(2, Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp inTs = rs.getTimestamp("check_in");
                    LocalDateTime inTime = inTs != null ? inTs.toLocalDateTime() : null;
                    Timestamp outTs = rs.getTimestamp("check_out");
                    LocalDateTime outTime = outTs != null ? outTs.toLocalDateTime() : null;
                    Date dVal = rs.getDate("attendance_date");
                    LocalDate d = dVal != null ? dVal.toLocalDate() : null;

                    return new attendance(
                        rs.getInt("attendance_id"),
                        rs.getInt("staff_id"),
                        inTime,
                        outTime,
                        d,
                        rs.getBoolean("verified")
                    );
                }
            }
        }
        return null;
    }

    public List<attendance> getAttendanceForStaff(int staffId) throws SQLException {
        List<attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE staff_id = ? ORDER BY attendance_date ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, staffId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Timestamp inTs = rs.getTimestamp("check_in");
                    LocalDateTime inTime = inTs != null ? inTs.toLocalDateTime() : null;
                    Timestamp outTs = rs.getTimestamp("check_out");
                    LocalDateTime outTime = outTs != null ? outTs.toLocalDateTime() : null;
                    Date dVal = rs.getDate("attendance_date");
                    LocalDate d = dVal != null ? dVal.toLocalDate() : null;

                    list.add(new attendance(
                        rs.getInt("attendance_id"),
                        rs.getInt("staff_id"),
                        inTime,
                        outTime,
                        d,
                        rs.getBoolean("verified")
                    ));
                }
            }
        }
        return list;
    }
}
