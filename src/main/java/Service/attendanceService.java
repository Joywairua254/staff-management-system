package Service;

import DAO.attendanceDAO;
import model.attendance;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class attendanceService {
    private attendanceDAO attendanceDAO;

    public attendanceService(Connection conn) {
        this.attendanceDAO = new attendanceDAO(conn);
    }

    public void recordAttendance(attendance att) throws SQLException {
        // Example rule: check-in must be before check-out
        if (att.getCheck_in() != null && att.getCheck_out() != null && att.getCheck_in().isAfter(att.getCheck_out())) {
            throw new IllegalArgumentException("Check-in cannot be after check-out!");
        }
        attendanceDAO.addAttendance(att);
    }

    public List<attendance> listAllAttendance() throws SQLException {
        return attendanceDAO.getAllAttendance();
    }

    public void updateAttendance(attendance att) throws SQLException {
        if (att.getCheck_in() != null && att.getCheck_out() != null && att.getCheck_in().isAfter(att.getCheck_out())) {
            throw new IllegalArgumentException("Check-in cannot be after check-out!");
        }
        attendanceDAO.updateAttendance(att);
    }

    public attendance getAttendanceForStaffDate(int staffId, java.time.LocalDate date) throws SQLException {
        return attendanceDAO.getAttendanceForStaffDate(staffId, date);
    }

    public List<attendance> getAttendanceForStaff(int staffId) throws SQLException {
        return attendanceDAO.getAttendanceForStaff(staffId);
    }
}
