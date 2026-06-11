package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DBConnection {

    // Database connection details
    private static final String URL = "jdbc:mariadb://localhost:3306/staffmanagement";
    private static final String USER = "root";          
    private static final String PASSWORD = ""; 

    // Method to establish and return a connection
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected successfully!");
        } catch (Exception e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
        return conn;
    }

    // A simple test method to check if the connection works
    public static void main(String[] args) {
        Connection conn = getConnection();
        if (conn != null) {
            try {
                // Run a simple query to test communication
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT 1");

                while (rs.next()) {
                    System.out.println("Test query result: " + rs.getInt(1));
                }

                // Close resources
                rs.close();
                stmt.close();
                conn.close();
                System.out.println("Connection closed properly.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
