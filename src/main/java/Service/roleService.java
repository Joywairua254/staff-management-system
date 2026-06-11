package Service;

import DAO.roleDAO;
import model.role;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class roleService {
    private roleDAO roleDAO;

    public roleService(Connection conn) {
        this.roleDAO = new roleDAO(conn);
    }

    public void addRole(role r) throws SQLException {
        roleDAO.addRole(r);
    }

    public List<role> listAllRoles() throws SQLException {
        return roleDAO.getAllRoles();
    }

    public void deleteRole(int id) throws SQLException {
        roleDAO.deleteRole(id);
    }
}
