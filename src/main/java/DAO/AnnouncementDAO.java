package DAO;

import model.Announcement;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDAO {
    private Connection conn;

    public AnnouncementDAO(Connection conn) {
        this.conn = conn;
    }

    public void addAnnouncement(Announcement ann) throws SQLException {
        String sql = "INSERT INTO announcement (title, content, posted_by) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, ann.getTitle());
            stmt.setString(2, ann.getContent());
            stmt.setString(3, ann.getPostedBy());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    ann.setAnnouncementId(rs.getInt(1));
                }
            }
        }
    }

    public List<Announcement> getAllAnnouncements() throws SQLException {
        List<Announcement> list = new ArrayList<>();
        String sql = "SELECT * FROM announcement ORDER BY created_at DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("created_at");
                LocalDateTime createdAt = ts != null ? ts.toLocalDateTime() : null;
                Announcement ann = new Announcement(
                    rs.getInt("announcement_id"),
                    rs.getString("title"),
                    rs.getString("content"),
                    createdAt,
                    rs.getString("posted_by")
                );
                list.add(ann);
            }
        }
        return list;
    }

    public void deleteAnnouncement(int id) throws SQLException {
        String sql = "DELETE FROM announcement WHERE announcement_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
