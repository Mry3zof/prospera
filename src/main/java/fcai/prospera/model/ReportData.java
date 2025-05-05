package fcai.prospera.model;

import java.util.Date;
import java.util.List;

public class ReportData {
    private User user;
    private ReportType type;
    private Date startDate;
    private Date endDate;
    private List<Asset> assets;
    private Date generatedAt;

    // Constructors
    public ReportData() {
        this.generatedAt = new Date();
    }

    public ReportData(User user, ReportType type, Date startDate, Date endDate,
                      List<Asset> assets) {
        this.user = user;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.assets = assets;
        this.generatedAt = new Date();
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public Date getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Date generatedAt) {
        this.generatedAt = generatedAt;
    }
}