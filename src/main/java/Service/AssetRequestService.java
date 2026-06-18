package Service;

import DAO.AssetRequestDAO;
import model.AssetRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AssetRequestService {
    private AssetRequestDAO assetRequestDAO;

    public AssetRequestService(Connection conn) {
        this.assetRequestDAO = new AssetRequestDAO(conn);
    }

    public void requestAsset(AssetRequest req) throws SQLException {
        if (req.getAssetName() == null || req.getAssetName().trim().isEmpty()) {
            throw new IllegalArgumentException("Asset name is required.");
        }
        if (req.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        if (req.getStatus() == null) {
            req.setStatus("Pending");
        }
        assetRequestDAO.addAssetRequest(req);
    }

    public List<AssetRequest> getAssetRequestsByStaff(int staffId) throws SQLException {
        return assetRequestDAO.getAssetRequestsByStaff(staffId);
    }

    public List<AssetRequest> getAllAssetRequests() throws SQLException {
        return assetRequestDAO.getAllAssetRequests();
    }

    public void updateAssetStatus(int requestId, String status) throws SQLException {
        if (status == null || (!status.equals("Approved") && !status.equals("Rejected") && !status.equals("Pending"))) {
            throw new IllegalArgumentException("Invalid status value.");
        }
        assetRequestDAO.updateAssetStatus(requestId, status);
    }
}
