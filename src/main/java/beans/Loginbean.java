package beans;

import Service.user_accountService;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import java.io.Serializable;
import util.DBConnection;
import java.sql.Connection;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

@Named("loginbean")
@SessionScoped
public class Loginbean implements Serializable{

    private String username;
    private String password;
    private boolean loggedIn;
    private int usertype; // 1 for admin, 0 for normal user

    private user_accountService userService;

    public Loginbean() {
        try {
            Connection conn = DBConnection.getConnection(); // JDBC helper
            userService = new user_accountService(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   public String login() {
        try {
            model.user_account user = userService.login(username, password);
            if (user != null) {
                loggedIn = true;
                this.usertype = user.getUsertype();
                return "dashboard?faces-redirect=true"; // ✅ redirect
            } else {
                loggedIn = false;
                this.usertype = 0;
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Invalid username or password", null));
                return null; // ❌ stay on login page
            }
        } catch (Exception e) {
            e.printStackTrace();
            loggedIn = false;
            return null;
        }
    }


    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        loggedIn = false;
        return "index?faces-redirect=true";
    }

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isLoggedIn() { return loggedIn; }
    public int getUsertype() { return usertype; }
    public void setUsertype(int usertype) { this.usertype = usertype; }
}
