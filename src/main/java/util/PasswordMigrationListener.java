package util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@WebListener
public class PasswordMigrationListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("PasswordMigrationListener: Starting password migration check...");
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                System.err.println("PasswordMigrationListener: Database connection is null. Migration skipped.");
                return;
            }

            // Ensure user_announcement_read table exists
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS user_announcement_read (" +
                             "staff_id INT NOT NULL, " +
                             "last_read_announcement_id INT NOT NULL, " +
                             "PRIMARY KEY (staff_id))");
                System.out.println("PasswordMigrationListener: Verified user_announcement_read table exists.");
            }

            // Ensure asset table exists and pre-populate if empty
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS asset (" +
                             "asset_id INT AUTO_INCREMENT PRIMARY KEY, " +
                             "name VARCHAR(255) NOT NULL UNIQUE, " +
                             "quantity INT NOT NULL DEFAULT 0, " +
                             "description VARCHAR(255))");
                System.out.println("PasswordMigrationListener: Verified asset table exists.");
                
                // Seed assets if empty
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM asset")) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        stmt.execute("INSERT INTO asset (name, quantity, description) VALUES " +
                                     "('Laptop', 10, 'Standard office laptop'), " +
                                     "('Ergonomic Chair', 5, 'Adjustable back support office chair'), " +
                                     "('Monitor 24\"', 8, 'Full HD dual monitor setup'), " +
                                     "('Keyboard & Mouse Combo', 15, 'Wireless keyboard and mouse combo'), " +
                                     "('Headset', 12, 'Noise cancelling usb headset')");
                        System.out.println("PasswordMigrationListener: Seeded asset table with default data.");
                    }
                }
            }
            
            // Get all users
            String selectSql = "SELECT user_id, password FROM user_account WHERE is_hashed = 0";
            String updateSql = "UPDATE user_account SET password = ?, is_hashed = 1 WHERE user_id = ?";
            
            int migratedCount = 0;
            try (Statement selectStmt = conn.createStatement();
                 ResultSet rs = selectStmt.executeQuery(selectSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                
                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String currentPassword = rs.getString("password");
                    
                    if (currentPassword == null) {
                        continue;
                    }
                    
                    // Check if it's already a BCrypt hash.
                    // BCrypt hashes start with $2a$, $2b$, or $2y$ and are typically 60 characters long.
                    boolean isHashed = currentPassword.startsWith("$2a$") || 
                                       currentPassword.startsWith("$2b$") || 
                                       currentPassword.startsWith("$2y$");
                    
                    String hashedPassword;
                    if (!isHashed) {
                        hashedPassword = BCrypt.hashpw(currentPassword, BCrypt.gensalt());
                    } else {
                        hashedPassword = currentPassword;
                    }
                    updateStmt.setString(1, hashedPassword);
                    updateStmt.setInt(2, userId);
                    updateStmt.addBatch();
                    migratedCount++;
                }
                
                if (migratedCount > 0) {
                    updateStmt.executeBatch();
                    System.out.println("PasswordMigrationListener: Successfully migrated " + migratedCount + " passwords (marked is_hashed = 1).");
                } else {
                    System.out.println("PasswordMigrationListener: No plain text passwords found to migrate.");
                }
            }
        } catch (Exception e) {
            System.err.println("PasswordMigrationListener: Error during password migration:");
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // No cleanup needed
    }
}
