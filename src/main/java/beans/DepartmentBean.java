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
import Service.departmentService;
import model.department;
import util.DBConnection;

@Named("departmentBean")
@SessionScoped
public class DepartmentBean implements Serializable {

    private List<department> departmentList;
    private department newDept;
    private department selectedDept;
    private departmentService deptService;

    @PostConstruct
    public void init() {
        Connection conn = DBConnection.getConnection();
        deptService = new departmentService(conn);
        if (conn == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Database Connection Error", "Could not connect to the database."));
            departmentList = new java.util.ArrayList<>();
            newDept = new department();
            selectedDept = new department();
            return;
        }
        loadDepartments();
        newDept = new department();
        selectedDept = new department();
    }

    public void loadDepartments() {
        try {
            departmentList = deptService.listAllDepartments();
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error loading departments", e.getMessage()));
            if (departmentList == null) {
                departmentList = new java.util.ArrayList<>();
            }
        }
    }

    public List<department> getDepartmentList() {
        return departmentList;
    }

    public department getNewDept() {
        return newDept;
    }

    public void setNewDept(department newDept) {
        this.newDept = newDept;
    }

    public department getSelectedDept() {
        return selectedDept;
    }

    public void setSelectedDept(department selectedDept) {
        this.selectedDept = selectedDept;
    }

    public void saveDepartment() {
        try {
            deptService.addDepartment(newDept);
            loadDepartments();
            
            // Also notify StaffBean to reload its lists/counts, since departments changed
            StaffBean staffBean = FacesContext.getCurrentInstance().getApplication()
                .evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{staffBean}", StaffBean.class);
            if (staffBean != null) {
                Connection conn = DBConnection.getConnection();
                staffBean.init();
            }

            newDept = new department(); // Reset form
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Department saved successfully!"));
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error saving department", e.getMessage()));
        }
    }

    public void prepareEdit(department d) {
        this.selectedDept = d;
    }

    public void updateDepartment() {
        try {
            Connection conn = DBConnection.getConnection();
            new departmentService(conn).updateDepartment(selectedDept);
            loadDepartments();
            
            // Notify StaffBean
            StaffBean staffBean = FacesContext.getCurrentInstance().getApplication()
                .evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{staffBean}", StaffBean.class);
            if (staffBean != null) {
                staffBean.init();
            }
            
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Department updated successfully!"));
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error updating department", e.getMessage()));
        }
    }

    public void delete(department d) {
        try {
            Connection conn = DBConnection.getConnection();
            new departmentService(conn).deleteDepartment(d.getDept_id());
            loadDepartments();
            
            // Notify StaffBean
            StaffBean staffBean = FacesContext.getCurrentInstance().getApplication()
                .evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{staffBean}", StaffBean.class);
            if (staffBean != null) {
                staffBean.init();
            }
            
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Department deleted successfully!"));
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error deleting department (make sure no staff belongs to it)", e.getMessage()));
        }
    }
}
