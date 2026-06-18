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
import Service.AnnouncementService;
import model.Announcement;
import util.DBConnection;

@Named("announcementBean")
@SessionScoped
public class AnnouncementBean implements Serializable {

    private List<Announcement> announcementsList;
    private String title;
    private String content;

    @PostConstruct
    public void init() {
        loadAnnouncements();
    }

    public void loadAnnouncements() {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                AnnouncementService service = new AnnouncementService(conn);
                announcementsList = service.listAllAnnouncements();
            } else {
                announcementsList = new java.util.ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (announcementsList == null) {
                announcementsList = new java.util.ArrayList<>();
            }
        }
    }

    public void postAnnouncement(String postedBy) {
        if (title == null || title.trim().isEmpty() || content == null || content.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Title and content cannot be empty."));
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                AnnouncementService service = new AnnouncementService(conn);
                Announcement ann = new Announcement();
                ann.setTitle(title.trim());
                ann.setContent(content.trim());
                ann.setPostedBy(postedBy);

                service.postAnnouncement(ann);

                // Reset form fields
                this.title = "";
                this.content = "";

                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Announcement posted successfully!"));
                loadAnnouncements();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not connect to the database."));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error posting announcement", e.getMessage()));
        }
    }

    public void deleteAnnouncement(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                AnnouncementService service = new AnnouncementService(conn);
                service.removeAnnouncement(id);
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Announcement deleted successfully!"));
                loadAnnouncements();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not connect to the database."));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error deleting announcement", e.getMessage()));
        }
    }

    // Getters and Setters
    public List<Announcement> getAnnouncementsList() {
        loadAnnouncements(); // Ensure fresh announcements feed
        return announcementsList;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
