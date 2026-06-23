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
import Service.LeaveRequestService;
import Service.StaffService;
import model.LeaveRequest;
import model.Staff;
import util.DBConnection;

@Named("leaveRequestBean")
@SessionScoped
public class LeaveRequestBean implements Serializable {

    private List<LeaveRequest> allRequests;
    private List<LeaveRequest> userRequests;
    private List<Staff> staffList;
    private LeaveRequest newRequest;
    private LeaveRequestService leaveService;
    private StaffService staffService;

    @PostConstruct
    public void init() {
        resetNewRequest();
        loadData();
    }

    public void loadData() {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            leaveService = new LeaveRequestService(conn);
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
            if (leaveService != null) {
                allRequests = leaveService.getAllLeaveRequests();
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
                leaveService = new LeaveRequestService(conn);
                userRequests = leaveService.getLeaveRequestsByStaff(staffId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            userRequests = new ArrayList<>();
        }
    }

    private void resetNewRequest() {
        newRequest = new LeaveRequest();
        newRequest.setStatus("Pending");
    }

    public void submitRequest(int staffId) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                leaveService = new LeaveRequestService(conn);
                newRequest.setStaffId(staffId);
                leaveService.requestLeave(newRequest);
                
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Leave request submitted successfully!"));
                
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

    public void approveRequest(int leaveId) {
        updateStatus(leaveId, "Approved");
    }

    public void rejectRequest(int leaveId) {
        updateStatus(leaveId, "Rejected");
    }

    private void updateStatus(int leaveId, String status) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                leaveService = new LeaveRequestService(conn);
                leaveService.updateLeaveStatus(leaveId, status);
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Leave request status updated to " + status + "."));
                loadAllRequests();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error updating status", e.getMessage()));
        }
    }

    public void cancelRequest(int leaveId, int staffId) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                leaveService = new LeaveRequestService(conn);
                boolean success = leaveService.cancelLeaveRequest(leaveId);
                if (success) {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Leave request cancelled successfully."));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Only pending leave requests can be cancelled."));
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
    public List<LeaveRequest> getAllRequests() {
        loadAllRequests();
        return allRequests;
    }

    public List<LeaveRequest> getUserRequests(int staffId) {
        loadUserRequests(staffId);
        return userRequests;
    }

    public List<LeaveRequest> getActiveLeaves() {
        List<LeaveRequest> active = new ArrayList<>();
        java.time.LocalDate today = java.time.LocalDate.now();
        loadAllRequests();
        if (allRequests != null) {
            for (LeaveRequest req : allRequests) {
                if ("Approved".equalsIgnoreCase(req.getStatus())) {
                    java.time.LocalDate start = req.getStartDate();
                    java.time.LocalDate end = req.getEndDate();
                    if (start != null && end != null && !today.isBefore(start) && !today.isAfter(end)) {
                        active.add(req);
                    }
                }
            }
        }
        return active;
    }

    public LeaveRequest getNewRequest() {
        return newRequest;
    }

    public void setNewRequest(LeaveRequest newRequest) {
        this.newRequest = newRequest;
    }
}
