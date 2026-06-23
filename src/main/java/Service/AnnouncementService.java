package Service;

import DAO.AnnouncementDAO;
import model.Announcement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AnnouncementService {
    private AnnouncementDAO announcementDAO;

    public AnnouncementService(Connection conn) {
        this.announcementDAO = new AnnouncementDAO(conn);
    }

    public void postAnnouncement(Announcement ann) throws SQLException {
        if (ann.getTitle() == null || ann.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty!");
        }
        if (ann.getContent() == null || ann.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty!");
        }
        announcementDAO.addAnnouncement(ann);
    }

    public List<Announcement> listAllAnnouncements() throws SQLException {
        return announcementDAO.getAllAnnouncements();
    }

    public void removeAnnouncement(int id) throws SQLException {
        announcementDAO.deleteAnnouncement(id);
    }

    public boolean hasUnreadAnnouncements(int staffId) throws SQLException {
        int maxId = announcementDAO.getMaxAnnouncementId();
        if (maxId == 0) {
            return false;
        }
        int lastRead = announcementDAO.getLastReadAnnouncementId(staffId);
        return maxId > lastRead;
    }

    public void markAnnouncementsAsRead(int staffId) throws SQLException {
        int maxId = announcementDAO.getMaxAnnouncementId();
        if (maxId > 0) {
            announcementDAO.setLastReadAnnouncementId(staffId, maxId);
        }
    }
}
