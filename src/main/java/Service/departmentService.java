package Service;

import DAO.departmentDAO;
import model.department;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class departmentService {
    private departmentDAO departmentDAO;

    public departmentService(Connection conn) {
        this.departmentDAO = new departmentDAO(conn);
    }

    public void addDepartment(department d) throws SQLException {
        departmentDAO.addDepartment(d);
    }

    public List<department> listAllDepartments() throws SQLException {
        return departmentDAO.getAllDepartments();
    }

    public void updateDepartment(department d) throws SQLException {
        departmentDAO.updateDepartment(d);
    }

    public void deleteDepartment(int id) throws SQLException {
        departmentDAO.deleteDepartment(id);
    }
}
