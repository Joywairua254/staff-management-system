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
import Service.StaffService;
import Service.departmentService;
import Service.roleService;
import Service.user_accountService;
import model.Staff;
import model.user_account;
import util.DBConnection;

@Named("staffBean")
@SessionScoped
public class StaffBean implements Serializable {

    private List<Staff> staffList;
    private Staff newStaff;
    private Staff selectedStaff;
    private StaffService staffService;
    private List<model.department> departments;
    private List<model.role> roles;
    private int deptCount = 0;
    private int roleCount = 0;

    // User account creation fields
    private boolean createAccount;
    private String username;
    private String password;
    private int usertype;

    @PostConstruct
    public void init() {
        Connection conn = DBConnection.getConnection();
        staffService = new StaffService(conn);
        if (conn == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Database Connection Error", "Could not connect to the database."));
            staffList = new java.util.ArrayList<>();
            newStaff = new Staff();
            selectedStaff = new Staff();
            return;
        }
        loadStaff();
        loadCounts(conn);
        loadDepartmentsAndRoles(conn);
        newStaff = new Staff();
        selectedStaff = new Staff();
    }

    public void loadStaff() {
        try {
            staffList = staffService.listStaff();
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error loading staff", e.getMessage()));
            if (staffList == null) {
                staffList = new java.util.ArrayList<>();
            }
        }
    }

    private void loadCounts(Connection conn) {
        if (conn == null) return;
        try {
            deptCount = new departmentService(conn).listAllDepartments().size();
            roleCount = new roleService(conn).listAllRoles().size();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDepartmentsAndRoles(Connection conn) {
        if (conn == null) return;
        try {
            departments = new departmentService(conn).listAllDepartments();
            roles = new roleService(conn).listAllRoles();
        } catch (Exception e) {
            e.printStackTrace();
            departments = new java.util.ArrayList<>();
            roles = new java.util.ArrayList<>();
        }
    }

    public List<Staff> getStaffList() {
        return staffList;
    }

    public Staff getNewStaff() {
        return newStaff;
    }

    public void setNewStaff(Staff newStaff) {
        this.newStaff = newStaff;
    }

    public Staff getSelectedStaff() {
        return selectedStaff;
    }

    public void setSelectedStaff(Staff selectedStaff) {
        this.selectedStaff = selectedStaff;
    }

    public int getDeptCount() {
        return deptCount;
    }

    public int getRoleCount() {
        return roleCount;
    }

    public List<model.department> getDepartments() {
        return departments;
    }

    public List<model.role> getRoles() {
        return roles;
    }

    public boolean isCreateAccount() {
        return createAccount;
    }

    public void setCreateAccount(boolean createAccount) {
        this.createAccount = createAccount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUsertype() {
        return usertype;
    }

    public void setUsertype(int usertype) {
        this.usertype = usertype;
    }

    public String saveStaff() {
        try {
            boolean success = staffService.saveStaff(newStaff);
            if (success) {
                if (createAccount) {
                    try {
                        user_account user = new user_account();
                        user.setUsername(username);
                        user.setPassword(password);
                        user.setRoleId(newStaff.getRoleId());
                        user.setStaffId(newStaff.getId());
                        user.setUsertype(usertype);

                        Connection conn = DBConnection.getConnection();
                        user_accountService uas = new user_accountService(conn);
                        uas.registerUser(user);
                        FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Staff and User Account created successfully!"));
                    } catch (Exception e) {
                        FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "Staff created, but user account failed: " + e.getMessage()));
                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Staff saved successfully!"));
                }

                loadStaff();
                
                // Also update counts since staff count changed
                Connection conn = DBConnection.getConnection();
                loadCounts(conn);

                newStaff = new Staff(); // Reset form
                createAccount = false;
                username = null;
                password = null;
                usertype = 0;
                return null; // Stay on the same page for AJAX dialog update
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to save staff."));
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error saving staff", e.getMessage()));
            return null;
        }
    }

    public void prepareEdit(Staff s) {
        this.selectedStaff = s;
    }

    public void updateStaff() {
        try {
            boolean success = staffService.updateStaff(selectedStaff);
            if (success) {
                loadStaff();
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Staff updated successfully!"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to update staff."));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error updating staff", e.getMessage()));
        }
    }

    public void delete(Staff s) {
        try {
            boolean success = staffService.deleteStaff(s.getId());
            if (success) {
                loadStaff();
                
                // Update stats counts
                Connection conn = DBConnection.getConnection();
                loadCounts(conn);

                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Staff deleted successfully!"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete staff."));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error deleting staff", e.getMessage()));
        }
    }
}
