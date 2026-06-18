package Service;

import DAO.user_accountDAO;
import model.user_account;
import org.mindrot.jbcrypt.BCrypt;
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
        // Hash the password with BCrypt
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(hashedPassword);
        }
        user.setIsHashed(true);
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
        String sql = "SELECT password, is_hashed FROM user_account WHERE username=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String dbPassword = rs.getString("password");
                    boolean isHashed = rs.getBoolean("is_hashed");
                    if (isHashed || (dbPassword != null && (dbPassword.startsWith("$2a$") || dbPassword.startsWith("$2b$") || dbPassword.startsWith("$2y$")))) {
                        return BCrypt.checkpw(password, dbPassword);
                    } else {
                        return password.equals(dbPassword);
                    }
                }
            }
        }
        return false;
    }

    // Authenticate and return user_account containing usertype
    public user_account login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM user_account WHERE username=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String dbPassword = rs.getString("password");
                    boolean isHashed = rs.getBoolean("is_hashed");
                    boolean matches = false;
                    if (isHashed || (dbPassword != null && (dbPassword.startsWith("$2a$") || dbPassword.startsWith("$2b$") || dbPassword.startsWith("$2y$")))) {
                        matches = BCrypt.checkpw(password, dbPassword);
                    } else {
                        matches = password.equals(dbPassword);
                    }
                    if (matches) {
                        user_account user = new user_account();
                        user.setUser_id(rs.getInt("user_id"));
                        user.setUsername(rs.getString("username"));
                        user.setPassword(dbPassword);
                        user.setStaffId(rs.getInt("staff_id"));
                        user.setUsertype(rs.getInt("usertype"));
                        user.setIsHashed(isHashed);
                        return user;
                    }
                }
            }
        }
        return null;
    }
}
