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
import Service.roleService;
import model.role;
import util.DBConnection;

@Named("roleBean")
@SessionScoped
public class RoleBean implements Serializable {

    private List<role> rolesList;
    private role newRole;
    private role selectedRole;
    private roleService roleService;

    @PostConstruct
    public void init() {
        Connection conn = DBConnection.getConnection();
        roleService = new roleService(conn);
        if (conn == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Database Connection Error", "Could not connect to the database."));
            rolesList = new java.util.ArrayList<>();
            newRole = new role();
            selectedRole = new role();
            return;
        }
        loadRoles();
        newRole = new role();
        selectedRole = new role();
    }

    public void loadRoles() {
        try {
            rolesList = roleService.listAllRoles();
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error loading roles", e.getMessage()));
            if (rolesList == null) {
                rolesList = new java.util.ArrayList<>();
            }
        }
    }

    public List<role> getRolesList() {
        return rolesList;
    }

    public role getNewRole() {
        return newRole;
    }

    public void setNewRole(role newRole) {
        this.newRole = newRole;
    }

    public role getSelectedRole() {
        return selectedRole;
    }

    public void setSelectedRole(role selectedRole) {
        this.selectedRole = selectedRole;
    }

    public String saveRole() {
        try {
            roleService.addRole(newRole);
            loadRoles();
            newRole = new role(); // Reset form
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Role saved successfully!"));
            return null; // Stay on the same page for AJAX dialog update
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error saving role", e.getMessage()));
            return null;
        }
    }

    public void prepareEdit(role r) {
        this.selectedRole = r;
    }

    public void updateRole() {
        try {
            Connection conn = DBConnection.getConnection();
            new roleService(conn).updateRole(selectedRole);
            loadRoles();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Role updated successfully!"));
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error updating role", e.getMessage()));
        }
    }

    public void delete(role r) {
        try {
            roleService.deleteRole(r.getRole_id());
            loadRoles();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Role deleted successfully!"));
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error deleting role", e.getMessage()));
        }
    }
}
