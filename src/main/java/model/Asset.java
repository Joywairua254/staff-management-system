package model;

import java.io.Serializable;

public class Asset implements Serializable {
    private int assetId;
    private String name;
    private int quantity;
    private String description;

    public Asset() {}

    public Asset(int assetId, String name, int quantity, String description) {
        this.assetId = assetId;
        this.name = name;
        this.quantity = quantity;
        this.description = description;
    }

    // Getters and Setters
    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
