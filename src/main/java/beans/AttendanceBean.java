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

// PrimeFaces Chart Imports
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.ChartData;

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

    // Chart properties
    private LineChartModel lineModel;
    private int currentChartStaffId = -1;

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
        loadAttendance(); // Ensure list is refreshed
        return attendanceList;
    }

    public attendance getNewAttendance() {
        return newAttendance;
    }

    public void setNewAttendance(attendance newAttendance) {
        this.newAttendance = newAttendance;
    }

    public List<Staff> getStaffList() {
        loadStaff();
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

    // Real-time Check-In for normal staff
    public void checkIn(int staffId) {
        try {
            Connection conn = DBConnection.getConnection();
            attendanceService attServ = new attendanceService(conn);
            
            attendance todayAtt = attServ.getAttendanceForStaffDate(staffId, LocalDate.now());
            if (todayAtt != null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "You have already checked in today!"));
                return;
            }
            
            attendance att = new attendance();
            att.setStaff_id(staffId);
            att.setAttendance_date(LocalDate.now());
            att.setCheck_in(LocalDateTime.now());
            att.setCheck_out(null);
            
            attServ.recordAttendance(att);
            loadAttendance();
            lineModel = null; // Clear cached chart model to refresh graph
            
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Checked in successfully!"));
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error checking in", e.getMessage()));
        }
    }

    // Real-time Check-Out for normal staff
    public void checkOut(int staffId) {
        try {
            Connection conn = DBConnection.getConnection();
            attendanceService attServ = new attendanceService(conn);
            
            attendance todayAtt = attServ.getAttendanceForStaffDate(staffId, LocalDate.now());
            if (todayAtt == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No check-in record found for today."));
                return;
            }
            
            if (todayAtt.getCheck_out() != null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "You have already checked out today!"));
                return;
            }
            
            todayAtt.setCheck_out(LocalDateTime.now());
            attServ.updateAttendance(todayAtt);
            loadAttendance();
            lineModel = null; // Clear cached chart model to refresh graph
            
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Checked out successfully!"));
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error checking out", e.getMessage()));
        }
    }

    // Fetch today's attendance for the logged-in staff
    public attendance getTodayAttendance(int staffId) {
        if (staffId <= 0) return null;
        try {
            Connection conn = DBConnection.getConnection();
            attendanceService attServ = new attendanceService(conn);
            return attServ.getAttendanceForStaffDate(staffId, LocalDate.now());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Chart model getter for UI
    public LineChartModel getAttendanceChart(int staffId) {
        if (lineModel == null || currentChartStaffId != staffId) {
            currentChartStaffId = staffId;
            createLineModel(staffId);
        }
        return lineModel;
    }

    private void createLineModel(int staffId) {
        lineModel = new LineChartModel();
        ChartData data = new ChartData();

        LineChartDataSet dataSet = new LineChartDataSet();
        List<Object> values = new java.util.ArrayList<>();
        List<String> labels = new java.util.ArrayList<>();

        try {
            Connection conn = DBConnection.getConnection();
            List<attendance> list = new attendanceService(conn).getAttendanceForStaff(staffId);
            // Limit to last 7 days of attendance
            if (list.size() > 7) {
                list = list.subList(list.size() - 7, list.size());
            }

            for (attendance att : list) {
                labels.add(att.getAttendance_date().toString());
                double hours = 0.0;
                if (att.getCheck_in() != null && att.getCheck_out() != null) {
                    long minutes = java.time.Duration.between(att.getCheck_in(), att.getCheck_out()).toMinutes();
                    hours = minutes / 60.0;
                }
                values.add(hours);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dataSet.setData(values);
        dataSet.setLabel("Hours Worked");
        dataSet.setFill(false);
        dataSet.setBorderColor("rgb(59, 130, 246)");
        dataSet.setTension(0.1);

        data.addChartDataSet(dataSet);
        data.setLabels(labels);

        lineModel.setData(data);

        LineChartOptions options = new LineChartOptions();
        lineModel.setOptions(options);
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
