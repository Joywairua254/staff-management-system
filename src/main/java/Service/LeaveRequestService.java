package Service;

import DAO.LeaveRequestDAO;
import model.LeaveRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class LeaveRequestService {
    private LeaveRequestDAO leaveRequestDAO;

    public LeaveRequestService(Connection conn) {
        this.leaveRequestDAO = new LeaveRequestDAO(conn);
    }

    public void requestLeave(LeaveRequest req) throws SQLException {
        if (req.getStartDate() == null || req.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and End date are required.");
        }
        if (req.getStartDate().isAfter(req.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after End date.");
        }
        if (req.getLeaveType() == null || req.getLeaveType().trim().isEmpty()) {
            throw new IllegalArgumentException("Leave type is required.");
        }
        if (req.getStatus() == null) {
            req.setStatus("Pending");
        }
        leaveRequestDAO.addLeaveRequest(req);
    }

    public List<LeaveRequest> getLeaveRequestsByStaff(int staffId) throws SQLException {
        return leaveRequestDAO.getLeaveRequestsByStaff(staffId);
    }

    public List<LeaveRequest> getAllLeaveRequests() throws SQLException {
        return leaveRequestDAO.getAllLeaveRequests();
    }

    public void updateLeaveStatus(int leaveId, String status) throws SQLException {
        if (status == null || (!status.equals("Approved") && !status.equals("Rejected") && !status.equals("Pending"))) {
            throw new IllegalArgumentException("Invalid status value.");
        }
        leaveRequestDAO.updateLeaveStatus(leaveId, status);
    }
}
