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
import model.AssetRequest;
import model.Staff;
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

    @PostConstruct
    public void init() {
        resetNewRequest();
        loadData();
    }

    public void loadData() {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            assetService = new AssetRequestService(conn);
            staffService = new StaffService(conn);
            loadAllRequests();
            loadStaffList();
        } else {
            allRequests = new ArrayList<>();
            userRequests = new ArrayList<>();
            staffList = new ArrayList<>();
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
                newRequest.setStaffId(staffId);
                assetService.requestAsset(newRequest);
                
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Asset request submitted successfully!"));
                
                resetNewRequest();
                loadUserRequests(staffId);
                loadAllRequests();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error submitting request", e.getMessage()));
        }
    }

    public void approveRequest(int requestId) {
        updateStatus(requestId, "Approved");
    }

    public void rejectRequest(int requestId) {
        updateStatus(requestId, "Rejected");
    }

    private void updateStatus(int requestId, String status) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                assetService = new AssetRequestService(conn);
                assetService.updateAssetStatus(requestId, status);
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Asset request status updated to " + status + "."));
                loadAllRequests();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error updating status", e.getMessage()));
        }
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
}
