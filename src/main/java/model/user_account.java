package model;

public class user_account {
    private int user_id;
    private String username;
    private String password;
    private int roleId;
    private int staffId;
    private int usertype;

    // Full constructor with all fields
    public user_account(int user_id, String username, String password, int roleId, int staffId) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.roleId = roleId;
        this.staffId = staffId;
    }

    // No-argument constructor (useful for frameworks like JSF/PrimeFaces)
    public user_account() {
    }

    // Getters and Setters
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
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

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public int getUsertype() {
        return usertype;
    }

    public void setUsertype(int usertype) {
        this.usertype = usertype;
    }

    @Override
    public String toString() {
        return "user_account{" +
                "user_id=" + user_id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", roleId=" + roleId +
                ", staffId=" + staffId +
                ", usertype=" + usertype +
                '}';
    }
}
