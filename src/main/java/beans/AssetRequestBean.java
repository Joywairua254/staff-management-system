package beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import Service.AssetRequestService;
import Service.StaffService;
import Service.AssetService;
import model.AssetRequest;
import model.Staff;
import model.Asset;
import util.DBConnection;

@Named("assetRequestBean")
@SessionScoped
public class AssetRequestBean implements Serializable {

    private List<AssetRequest> allRequests;
    private List<AssetRequest> userRequests;
    private List<Staff> staffList;
    private AssetRequest newRequest;
    private AssetRequestService assetService;
    private StaffService staffService;

    // Available assets inventory fields
    private List<Asset> availableAssets;
    private Asset newAsset = new Asset();
    private Asset selectedAsset;
    private AssetService assetInventoryService;

    @PostConstruct
    public void init() {
        resetNewRequest();
        loadData();
        loadAvailableAssets();
    }

    public void loadData() {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            assetService = new AssetRequestService(conn);
            staffService = new StaffService(conn);
            loadAllRequests();
            loadStaffList();
            loadAvailableAssets();
        } else {
            allRequests = new ArrayList<>();
            userRequests = new ArrayList<>();
            staffList = new ArrayList<>();
            availableAssets = new ArrayList<>();
        }
    }

    public void loadAllRequests() {
        try {
            if (assetService != null) {
                allRequests = assetService.getAllAssetRequests();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            allRequests = new ArrayList<>();
        }
    }

    public void loadStaffList() {
        try {
            if (staffService != null) {
                staffList = staffService.listStaff();
            }
        } catch (Exception e) {
            e.printStackTrace();
            staffList = new ArrayList<>();
        }
    }

    public void loadUserRequests(int staffId) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                assetService = new AssetRequestService(conn);
                userRequests = assetService.getAssetRequestsByStaff(staffId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            userRequests = new ArrayList<>();
        }
    }

    private void resetNewRequest() {
        newRequest = new AssetRequest();
        newRequest.setQuantity(1);
        newRequest.setStatus("Pending");
    }

    public void submitRequest(int staffId) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                assetService = new AssetRequestService(conn);
                AssetService inventoryService = new AssetService(conn);
                Asset asset = inventoryService.getAssetByName(newRequest.getAssetName());
                if (asset == null) {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Selected asset is not available in the inventory."));
                    return;
                }
                if (asset.getQuantity() < newRequest.getQuantity()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Requested quantity (" + newRequest.getQuantity() + ") exceeds available stock (" + asset.getQuantity() + ")."));
                    return;
                }

                newRequest.setStaffId(staffId);
                assetService.requestAsset(newRequest);
                
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Asset request submitted successfully!"));
                
                resetNewRequest();
                loadUserRequests(staffId);
                loadAllRequests();
                loadAvailableAssets();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error submitting request", e.getMessage()));
        }
    }

    public void approveRequest(int requestId) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                assetService = new AssetRequestService(conn);
                AssetRequest req = assetService.getAssetRequestById(requestId);
                if (req != null) {
                    AssetService inventoryService = new AssetService(conn);
                    Asset asset = inventoryService.getAssetByName(req.getAssetName());
                    if (asset == null) {
                        FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Approval Failed", 
                            "The asset '" + req.getAssetName() + "' does not exist in the inventory."));
                        return;
                    }
                    if (asset.getQuantity() < req.getQuantity()) {
                        FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Approval Failed", 
                            "Insufficient stock: only " + asset.getQuantity() + " available, but " + req.getQuantity() + " requested."));
                        return;
                    }
                    
                    // Deduct stock and update status
                    asset.setQuantity(asset.getQuantity() - req.getQuantity());
                    inventoryService.updateAsset(asset);
                    
                    assetService.updateAssetStatus(requestId, "Approved");
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", 
                        "Asset request approved. Deducted " + req.getQuantity() + " from inventory stock."));
                    
                    loadAllRequests();
                    loadAvailableAssets();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error approving request", e.getMessage()));
        }
    }

    public void rejectRequest(int requestId) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                assetService = new AssetRequestService(conn);
                assetService.updateAssetStatus(requestId, "Rejected");
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Asset request rejected."));
                loadAllRequests();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error rejecting request", e.getMessage()));
        }
    }

    public void cancelRequest(int requestId, int staffId) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                assetService = new AssetRequestService(conn);
                boolean success = assetService.cancelAssetRequest(requestId);
                if (success) {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Asset request cancelled successfully."));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Only pending asset requests can be cancelled."));
                }
                loadUserRequests(staffId);
                loadAllRequests();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error cancelling request", e.getMessage()));
        }
    }

    public void loadAvailableAssets() {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                assetInventoryService = new AssetService(conn);
                availableAssets = assetInventoryService.getAllAssets();
            } else {
                availableAssets = new ArrayList<>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            availableAssets = new ArrayList<>();
        }
    }

    public void addAsset() {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                assetInventoryService = new AssetService(conn);
                assetInventoryService.addAsset(newAsset);
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Asset added to inventory successfully!"));
                newAsset = new Asset();
                loadAvailableAssets();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error adding asset", e.getMessage()));
        }
    }

    public void selectAssetForEdit(Asset asset) {
        this.selectedAsset = new Asset(asset.getAssetId(), asset.getName(), asset.getQuantity(), asset.getDescription());
    }

    public void updateAsset() {
        try {
            if (selectedAsset == null) return;
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                assetInventoryService = new AssetService(conn);
                assetInventoryService.updateAsset(selectedAsset);
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Asset updated successfully!"));
                selectedAsset = null;
                loadAvailableAssets();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error updating asset", e.getMessage()));
        }
    }

    public void deleteAsset(int assetId) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                assetInventoryService = new AssetService(conn);
                assetInventoryService.deleteAsset(assetId);
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Asset deleted from inventory successfully."));
                loadAvailableAssets();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error deleting asset", e.getMessage()));
        }
    }

    public void cancelEdit() {
        selectedAsset = null;
    }

    public String getAssetAvailableQuantity(String name) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                AssetService inventoryService = new AssetService(conn);
                Asset asset = inventoryService.getAssetByName(name);
                if (asset != null) {
                    return String.valueOf(asset.getQuantity());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0 (Not in inventory)";
    }

    public String getStaffName(int staffId) {
        if (staffList != null) {
            for (Staff s : staffList) {
                if (s.getId() == staffId) {
                    return s.getFirstName() + " " + s.getLastName();
                }
            }
        }
        return "Unknown Staff (ID: " + staffId + ")";
    }

    // Getters and Setters
    public List<AssetRequest> getAllRequests() {
        loadAllRequests();
        return allRequests;
    }

    public List<AssetRequest> getUserRequests(int staffId) {
        loadUserRequests(staffId);
        return userRequests;
    }

    public AssetRequest getNewRequest() {
        return newRequest;
    }

    public void setNewRequest(AssetRequest newRequest) {
        this.newRequest = newRequest;
    }

    public List<Asset> getAvailableAssets() {
        loadAvailableAssets();
        return availableAssets;
    }

    public void setAvailableAssets(List<Asset> availableAssets) {
        this.availableAssets = availableAssets;
    }

    public Asset getNewAsset() {
        return newAsset;
    }

    public void setNewAsset(Asset newAsset) {
        this.newAsset = newAsset;
    }

    public Asset getSelectedAsset() {
        return selectedAsset;
    }

    public void setSelectedAsset(Asset selectedAsset) {
        this.selectedAsset = selectedAsset;
    }
}
