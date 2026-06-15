package DAO;

import model.Staff;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {
    private Connection conn;

    public StaffDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insertStaff(Staff staff) throws SQLException {
        String sql = "INSERT INTO staff (F_name, L_Name, email, phone, dept_id, role_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, staff.getFirstName());
            ps.setString(2, staff.getLastName());
            ps.setString(3, staff.getEmail());
            ps.setString(4, staff.getPhone());
            ps.setInt(5, staff.getDeptId());
            ps.setInt(6, staff.getRoleId());
            boolean result = ps.executeUpdate() == 1;
            if (result) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        staff.setId(rs.getInt(1));
                    }
                }
            }
            return result;
        }
    }

    public List<Staff> getAllStaff() throws SQLException {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT staff_id, F_name, L_Name, email, phone, dept_id, role_id FROM staff";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Staff s = new Staff(
                    rs.getInt("staff_id"),
                    rs.getString("F_name"),
                    rs.getString("L_Name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getInt("dept_id"),
                    rs.getInt("role_id")
                );
                list.add(s);
            }
        }
        return list;
    }

    public boolean deleteStaff(int id) throws SQLException {
        boolean autoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);
            
            // Delete user accounts first
            String deleteUsersSql = "DELETE FROM user_account WHERE staff_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteUsersSql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            
            // Delete attendance records next
            String deleteAttendanceSql = "DELETE FROM attendance WHERE staff_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteAttendanceSql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            
            // Delete staff record
            String sql = "DELETE FROM staff WHERE staff_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                boolean result = ps.executeUpdate() == 1;
                conn.commit();
                return result;
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(autoCommit);
        }
    }

    public boolean updateStaff(Staff staff) throws SQLException {
        String sql = "UPDATE staff SET F_name = ?, L_Name = ?, email = ?, phone = ?, dept_id = ?, role_id = ? WHERE staff_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, staff.getFirstName());
            ps.setString(2, staff.getLastName());
            ps.setString(3, staff.getEmail());
            ps.setString(4, staff.getPhone());
            ps.setInt(5, staff.getDeptId());
            ps.setInt(6, staff.getRoleId());
            ps.setInt(7, staff.getId());
            return ps.executeUpdate() == 1;
        }
    }

    public Staff getStaffById(int id) throws SQLException {
        String sql = "SELECT staff_id, F_name, L_Name, email, phone, dept_id, role_id FROM staff WHERE staff_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Staff(
                        rs.getInt("staff_id"),
                        rs.getString("F_name"),
                        rs.getString("L_Name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getInt("dept_id"),
                        rs.getInt("role_id")
                    );
                }
            }
        }
        return null;
    }
}

