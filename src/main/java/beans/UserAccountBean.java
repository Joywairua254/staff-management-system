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
import Service.user_accountService;
import Service.StaffService;
import model.user_account;
import model.Staff;
import util.DBConnection;

@Named("userAccountBean")
@SessionScoped
public class UserAccountBean implements Serializable {

    private List<user_account> userList;
    private user_account newUser;
    private List<Staff> staffList;

    @PostConstruct
    public void init() {
        newUser = new user_account();
        loadUsers();
        loadStaff();
    }

    public void loadUsers() {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                user_accountService userService = new user_accountService(conn);
                userList = userService.listAllUsers();
            } else {
                userList = new java.util.ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error loading users", e.getMessage()));
            if (userList == null) {
                userList = new java.util.ArrayList<>();
            }
        }
    }

    public void loadStaff() {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                StaffService staffService = new StaffService(conn);
                staffList = staffService.listStaff();
            } else {
                staffList = new java.util.ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (staffList == null) {
                staffList = new java.util.ArrayList<>();
            }
        }
    }

    public void saveUser() {
        if (newUser.getUsername() == null || newUser.getUsername().trim().isEmpty() ||
            newUser.getPassword() == null || newUser.getPassword().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Username and password cannot be empty."));
            return;
        }
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                user_accountService userService = new user_accountService(conn);
                userService.registerUser(newUser);
                newUser = new user_account(); // Reset
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "User account created successfully!"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not connect to the database."));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error creating user", e.getMessage()));
        }
        loadUsers();
    }

    public void delete(user_account u) {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                user_accountService userService = new user_accountService(conn);
                userService.deleteUser(u.getUser_id());
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "User account deleted successfully!"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not connect to the database."));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error deleting user", e.getMessage()));
        }
        loadUsers();
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

    // Getters and Setters
    public List<user_account> getUserList() { 
        loadUsers();
        loadStaff(); // Ensure staff name cache is populated with fresh staff records!
        return userList; 
    }
    public user_account getNewUser() { return newUser; }
    public void setNewUser(user_account newUser) { this.newUser = newUser; }
    public List<Staff> getStaffList() { 
        loadStaff();
        return staffList; 
    }
}
