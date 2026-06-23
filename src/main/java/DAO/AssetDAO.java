package DAO;

import model.Asset;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssetDAO {
    private Connection conn;

    public AssetDAO(Connection conn) {
        this.conn = conn;
    }

    public void addAsset(Asset asset) throws SQLException {
        String sql = "INSERT INTO asset (name, quantity, description) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, asset.getName());
            stmt.setInt(2, asset.getQuantity());
            stmt.setString(3, asset.getDescription());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    asset.setAssetId(rs.getInt(1));
                }
            }
        }
    }

    public List<Asset> getAllAssets() throws SQLException {
        List<Asset> list = new ArrayList<>();
        String sql = "SELECT * FROM asset ORDER BY name ASC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Asset(
                    rs.getInt("asset_id"),
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    rs.getString("description")
                ));
            }
        }
        return list;
    }

    public Asset getAssetByName(String name) throws SQLException {
        String sql = "SELECT * FROM asset WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Asset(
                        rs.getInt("asset_id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getString("description")
                    );
                }
            }
        }
        return null;
    }

    public void updateAsset(Asset asset) throws SQLException {
        String sql = "UPDATE asset SET name = ?, quantity = ?, description = ? WHERE asset_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, asset.getName());
            stmt.setInt(2, asset.getQuantity());
            stmt.setString(3, asset.getDescription());
            stmt.setInt(4, asset.getAssetId());
            stmt.executeUpdate();
        }
    }

    public void deleteAsset(int assetId) throws SQLException {
        String sql = "DELETE FROM asset WHERE asset_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, assetId);
            stmt.executeUpdate();
        }
    }
}
