package model;

public class department {
    private int dept_id;       
    private String dept_name;   
    
    // Default Constructor
    public department() {}
    
    // Constructor
    public department(int dept_id, String dept_name) {
        this.dept_id = dept_id;
        this.dept_name = dept_name;
    }

    // Getters and Setters
    public int getDept_id() { return dept_id; }
    public void setDept_id(int dept_id) { this.dept_id = dept_id; }

    public String getDept_name() { return dept_name; }
    public void setDept_name(String dept_name) { this.dept_name = dept_name; }
}
