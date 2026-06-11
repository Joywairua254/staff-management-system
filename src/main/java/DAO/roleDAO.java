package DAO;

import model.role;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class roleDAO {
    private Connection conn;

    public roleDAO(Connection conn) {
        this.conn = conn;
    }

    public void addRole(role role) throws SQLException {
        String sql = "INSERT INTO role (role_id, role_name) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, role.getRole_id());   
            stmt.setString(2, role.getRole_name());
            stmt.executeUpdate();
        }
    }

    public List<role> getAllRoles() throws SQLException {
        List<role> roleList = new ArrayList<>();
        String sql = "SELECT role_id, role_name FROM role";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                role role = new role(
                    rs.getInt("role_id"),
                    rs.getString("role_name")
                );
                roleList.add(role);
            }
        }
        return roleList;
    }

    public void deleteRole(int id) throws SQLException {
        String sql = "DELETE FROM role WHERE role_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
