package com.project.fujicraft_management_system.Request.dto;
public class MergedItemDetails {
    private String itemName;
    private String itemColor;
    private int poBalanceQuantity;
    private int stockBalanceQuantity;
    private int cavity;
    private double weightPerPiece;
    private double cycleTime;

    // Constructor that matches the fields selected in the query
    public MergedItemDetails(String itemName, String itemColor, int poBalanceQuantity,
                             int stockBalanceQuantity, int cavity, double weightPerPiece, double cycleTime) {
        this.itemName = itemName;
        this.itemColor = itemColor;
        this.poBalanceQuantity = poBalanceQuantity;
        this.stockBalanceQuantity = stockBalanceQuantity;
        this.cavity = cavity;
        this.weightPerPiece = weightPerPiece;
        this.cycleTime = cycleTime;
    }

    // Getters and setters (if required)
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    // Define other getters and setters similarly

    public int getPoBalanceQuantity() {
        return poBalanceQuantity;
    }

    public void setPoBalanceQuantity(int poBalanceQuantity) {
        this.poBalanceQuantity = poBalanceQuantity;
    }

    public String getItemColor() {
        return itemColor;
    }

    public void setItemColor(String itemColor) {
        this.itemColor = itemColor;
    }

    public int getStockBalanceQuantity() {
        return stockBalanceQuantity;
    }

    public void setStockBalanceQuantity(int stockBalanceQuantity) {
        this.stockBalanceQuantity = stockBalanceQuantity;
    }

    public int getCavity() {
        return cavity;
    }

    public void setCavity(int cavity) {
        this.cavity = cavity;
    }

    public double getWeightPerPiece() {
        return weightPerPiece;
    }

    public void setWeightPerPiece(double weightPerPiece) {
        this.weightPerPiece = weightPerPiece;
    }

    public double getCycleTime() {
        return cycleTime;
    }

    public void setCycleTime(double cycleTime) {
        this.cycleTime = cycleTime;
    }
}
