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
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import Service.attendanceService;
import Service.StaffService;
import model.attendance;
import model.Staff;
import util.DBConnection;

@Named("attendanceBean")
@SessionScoped
public class AttendanceBean implements Serializable {

    private List<attendance> attendanceList;
    private attendance newAttendance;
    private attendanceService attService;
    private List<Staff> staffList;
    private StaffService staffService;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;

    @PostConstruct
    public void init() {
        Connection conn = DBConnection.getConnection();
        attService = new attendanceService(conn);
        staffService = new StaffService(conn);
        if (conn == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Database Connection Error", "Could not connect to the database."));
            attendanceList = new java.util.ArrayList<>();
            staffList = new java.util.ArrayList<>();
            resetNewAttendance();
            return;
        }
        loadAttendance();
        loadStaff();
        resetNewAttendance();
    }

    public void loadAttendance() {
        try {
            attendanceList = attService.listAllAttendance();
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error loading attendance logs", e.getMessage()));
            if (attendanceList == null) {
                attendanceList = new java.util.ArrayList<>();
            }
        }
    }

    public void loadStaff() {
        try {
            staffList = staffService.listStaff();
        } catch (Exception e) {
            e.printStackTrace();
            if (staffList == null) {
                staffList = new java.util.ArrayList<>();
            }
        }
    }

    private void resetNewAttendance() {
        newAttendance = new attendance();
        newAttendance.setAttendance_date(LocalDate.now());
        checkInTime = LocalTime.of(8, 0, 0);
        checkOutTime = LocalTime.of(17, 0, 0);
    }

    public List<attendance> getAttendanceList() {
        return attendanceList;
    }

    public attendance getNewAttendance() {
        return newAttendance;
    }

    public void setNewAttendance(attendance newAttendance) {
        this.newAttendance = newAttendance;
    }

    public List<Staff> getStaffList() {
        return staffList;
    }

    public String getStaffName(int staffId) {
        if (staffList != null) {
            for (Staff s : staffList) {
                if (s.getId() == staffId) {
                    return s.getFirstName() + " " + s.getLastName();
                }
            }
        }
        return "Unknown (ID: " + staffId + ")";
    }

    public void saveAttendance() {
        try {
            LocalDate date = newAttendance.getAttendance_date();
            if (date != null) {
                if (checkInTime != null) {
                    newAttendance.setCheck_in(LocalDateTime.of(date, checkInTime));
                }
                if (checkOutTime != null) {
                    newAttendance.setCheck_out(LocalDateTime.of(date, checkOutTime));
                }
            }
            attService.recordAttendance(newAttendance);
            loadAttendance();
            resetNewAttendance();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Attendance marked successfully!"));
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error marking attendance", e.getMessage()));
        }
    }

    public LocalTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }
}
