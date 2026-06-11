package Service;

import DAO.StaffDAO;
import model.Staff;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class StaffService {
    private StaffDAO staffDAO;

    public StaffService(Connection conn) {
        this.staffDAO = new StaffDAO(conn);
    }

    public boolean saveStaff(Staff staff) throws SQLException {
        return staffDAO.insertStaff(staff);
    }

    public List<Staff> listStaff() throws SQLException {
        return staffDAO.getAllStaff();
    }

    public boolean deleteStaff(int id) throws SQLException {
        return staffDAO.deleteStaff(id);
    }
}
