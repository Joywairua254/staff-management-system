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
}
