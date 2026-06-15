package beans;

import Service.user_accountService;
import Service.StaffService;
import Service.departmentService;
import Service.roleService;
import model.Staff;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import java.io.Serializable;
import util.DBConnection;
import java.sql.Connection;
import java.util.List;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

@Named("loginbean")
@SessionScoped
public class Loginbean implements Serializable {

    private String username;
    private String password;
    private boolean loggedIn;
    private int usertype; // 1 for admin, 0 for normal user
    private int staffId;
    private Staff currentStaff;
    private String departmentName;
    private String roleName;

    private user_accountService userService;

    public Loginbean() {
        try {
            Connection conn = DBConnection.getConnection(); // JDBC helper
            userService = new user_accountService(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String login() {
        try {
            model.user_account user = userService.login(username, password);
            if (user != null) {
                loggedIn = true;
                this.usertype = user.getUsertype();
                this.staffId = user.getStaffId();
                
                if (this.staffId > 0) {
                    Connection conn = DBConnection.getConnection();
                    StaffService ss = new StaffService(conn);
                    this.currentStaff = ss.getStaffById(this.staffId);
                    if (this.currentStaff != null) {
                        // Find department name
                        List<model.department> depts = new departmentService(conn).listAllDepartments();
                        for (model.department d : depts) {
                            if (d.getDept_id() == this.currentStaff.getDeptId()) {
                                this.departmentName = d.getDept_name();
                                break;
                            }
                        }
                        // Find role name
                        List<model.role> roles = new roleService(conn).listAllRoles();
                        for (model.role r : roles) {
                            if (r.getRole_id() == this.currentStaff.getRoleId()) {
                                this.roleName = r.getRole_name();
                                break;
                            }
                        }
                    }
                }
                return "dashboard?faces-redirect=true"; // redirect
            } else {
                loggedIn = false;
                this.usertype = 0;
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Invalid username or password", null));
                return null; // ❌ stay on login page
            }
        } catch (Exception e) {
            e.printStackTrace();
            loggedIn = false;
            return null;
        }
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        loggedIn = false;
        return "index?faces-redirect=true";
    }

    public void saveProfile() {
        try {
            if (currentStaff != null) {
                Connection conn = DBConnection.getConnection();
                StaffService ss = new StaffService(conn);
                boolean success = ss.updateStaff(currentStaff);
                if (success) {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Profile updated successfully!"));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to update profile."));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error updating profile", e.getMessage()));
        }
    }

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isLoggedIn() { return loggedIn; }
    public int getUsertype() { return usertype; }
    public void setUsertype(int usertype) { this.usertype = usertype; }
    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }
    public Staff getCurrentStaff() { return currentStaff; }
    public void setCurrentStaff(Staff currentStaff) { this.currentStaff = currentStaff; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
}
