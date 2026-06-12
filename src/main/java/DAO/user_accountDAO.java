package DAO;

import model.user_account;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class user_accountDAO {
    private Connection conn;

    public user_accountDAO(Connection conn) {
        this.conn = conn;
    }

    // Add a new user account
    public void addUser(user_account user) throws SQLException {
        String sql = "INSERT INTO user_account (username, password, usertype, staff_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setInt(3, user.getUsertype());   // usertype field
            stmt.setInt(4, user.getStaffId());    // staff_id field
            stmt.executeUpdate();
        }
    }

    // Delete a user account
    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM user_account WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    // Fetch all user accounts
    public List<user_account> getAllUsers() throws SQLException {
        List<user_account> user_accountList = new ArrayList<>();
        String sql = "SELECT * FROM user_account";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                user_account user = new user_account(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    0, // roleId set to default 0
                    rs.getInt("staff_id")   // include staff_id in constructor
                );
                user.setUsertype(rs.getInt("usertype")); // map usertype
                user_accountList.add(user);
            }
        }
        return user_accountList;
    }
}
