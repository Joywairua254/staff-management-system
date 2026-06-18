package model;

import java.time.LocalDateTime;

public class AssetRequest {
    private int requestId;
    private int staffId;
    private String assetName;
    private int quantity;
    private String reason;
    private String status; // Pending, Approved, Rejected
    private LocalDateTime createdAt;

    public AssetRequest() {}

    public AssetRequest(int requestId, int staffId, String assetName, int quantity, String reason, String status, LocalDateTime createdAt) {
        this.requestId = requestId;
        this.staffId = staffId;
        this.assetName = assetName;
        this.quantity = quantity;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
