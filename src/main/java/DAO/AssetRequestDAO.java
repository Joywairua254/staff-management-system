package DAO;

import model.AssetRequest;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AssetRequestDAO {
    private Connection conn;

    public AssetRequestDAO(Connection conn) {
        this.conn = conn;
    }

    public void addAssetRequest(AssetRequest req) throws SQLException {
        String sql = "INSERT INTO asset_request (staff_id, asset_name, quantity, reason, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, req.getStaffId());
            stmt.setString(2, req.getAssetName());
            stmt.setInt(3, req.getQuantity());
            stmt.setString(4, req.getReason());
            stmt.setString(5, req.getStatus());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    req.setRequestId(rs.getInt(1));
                }
            }
        }
    }

    public List<AssetRequest> getAssetRequestsByStaff(int staffId) throws SQLException {
        List<AssetRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM asset_request WHERE staff_id = ? ORDER BY created_at DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, staffId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public List<AssetRequest> getAllAssetRequests() throws SQLException {
        List<AssetRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM asset_request ORDER BY created_at DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public void updateAssetStatus(int requestId, String status) throws SQLException {
        String sql = "UPDATE asset_request SET status = ? WHERE request_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            stmt.executeUpdate();
        }
    }

    private AssetRequest mapRow(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime created = ts != null ? ts.toLocalDateTime() : null;

        return new AssetRequest(
            rs.getInt("request_id"),
            rs.getInt("staff_id"),
            rs.getString("asset_name"),
            rs.getInt("quantity"),
            rs.getString("reason"),
            rs.getString("status"),
            created
        );
    }
}
