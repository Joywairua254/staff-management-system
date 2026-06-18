package model;

import java.time.LocalDateTime;
import java.time.LocalDate;

public class attendance {
    private int attendance_id;      
    private int staff_id;           
    private LocalDateTime check_in; 
    private LocalDateTime check_out;
    private LocalDate attendance_date;

    private boolean verified;

    // Default Constructor
    public attendance() {}

    // Constructor
    public attendance(int attendance_id, int staff_id, LocalDateTime check_in, LocalDateTime check_out, LocalDate attendance_date) {
        this.attendance_id = attendance_id;
        this.staff_id = staff_id;
        this.check_in = check_in;
        this.check_out = check_out;
        this.attendance_date = attendance_date;
    }

    public attendance(int attendance_id, int staff_id, LocalDateTime check_in, LocalDateTime check_out, LocalDate attendance_date, boolean verified) {
        this.attendance_id = attendance_id;
        this.staff_id = staff_id;
        this.check_in = check_in;
        this.check_out = check_out;
        this.attendance_date = attendance_date;
        this.verified = verified;
    }

    // Getters and Setters
    public int getAttendance_id() { return attendance_id; }
    public void setAttendance_id(int attendance_id) { this.attendance_id = attendance_id; }

    public int getStaff_id() { return staff_id; }
    public void setStaff_id(int staff_id) { this.staff_id = staff_id; }

    public LocalDateTime getCheck_in() { return check_in; }
    public void setCheck_in(LocalDateTime check_in) { this.check_in = check_in; }

    public LocalDateTime getCheck_out() { return check_out; }
    public void setCheck_out(LocalDateTime check_out) { this.check_out = check_out; }

    public LocalDate getAttendance_date() { return attendance_date; }
    public void setAttendance_date(LocalDate attendance_date) { this.attendance_date = attendance_date; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
}
