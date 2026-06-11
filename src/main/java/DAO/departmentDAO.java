package DAO; 

import model.department; 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class departmentDAO {
    private Connection conn; 

    public departmentDAO(Connection conn) {
        this.conn = conn; 
    }

    
    public void addDepartment(department dept) throws SQLException {
        String sql = "INSERT INTO department (dept_name) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, dept.getDept_name());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    dept.setDept_id(rs.getInt(1));
                }
            }
        }
    }

    
    public List<department> getAllDepartments() throws SQLException {
        List<department> deptList = new ArrayList<>();
        String sql = "SELECT * FROM department";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            department dept = new department(
                rs.getInt("dept_id"),
                rs.getString("dept_name")
            );
            deptList.add(dept);
        }
        return deptList;
    }
}
