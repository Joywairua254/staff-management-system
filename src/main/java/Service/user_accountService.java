package Service;

import DAO.user_accountDAO;
import model.user_account;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class user_accountService {
    private user_accountDAO userDAO;
    private Connection conn;

    public user_accountService(Connection conn) {
        this.conn = conn;
        this.userDAO = new user_accountDAO(conn);
    }

    // Business logic: register new user
    public void registerUser(user_account user) throws SQLException {
        user.setUsername(user.getUsername().toLowerCase());
        userDAO.addUser(user);
    }

    // Business logic: list all users
    public List<user_account> listAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    // Business logic: delete user account
    public void deleteUser(int userId) throws SQLException {
        userDAO.deleteUser(userId);
    }

    // ✅ New method: validate login credentials
    public boolean validateUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM user_account WHERE username=? AND password=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // true if a match is found
        }
    }

    // Authenticate and return user_account containing usertype
    public user_account login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM user_account WHERE username=? AND password=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user_account user = new user_account();
                    user.setUser_id(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setStaffId(rs.getInt("staff_id"));
                    user.setUsertype(rs.getInt("usertype"));
                    return user;
                }
            }
        }
        return null;
    }
}
