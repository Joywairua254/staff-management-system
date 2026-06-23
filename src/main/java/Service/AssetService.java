package Service;

import DAO.AssetDAO;
import model.Asset;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AssetService {
    private AssetDAO assetDAO;

    public AssetService(Connection conn) {
        this.assetDAO = new AssetDAO(conn);
    }

    public void addAsset(Asset asset) throws SQLException {
        if (asset.getName() == null || asset.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Asset name is required.");
        }
        if (asset.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        assetDAO.addAsset(asset);
    }

    public List<Asset> getAllAssets() throws SQLException {
        return assetDAO.getAllAssets();
    }

    public Asset getAssetByName(String name) throws SQLException {
        return assetDAO.getAssetByName(name);
    }

    public void updateAsset(Asset asset) throws SQLException {
        if (asset.getName() == null || asset.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Asset name is required.");
        }
        if (asset.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        assetDAO.updateAsset(asset);
    }

    public void deleteAsset(int assetId) throws SQLException {
        assetDAO.deleteAsset(assetId);
    }
}
