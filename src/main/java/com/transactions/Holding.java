package com.transactions;

public class Holding implements java.io.Serializable{
    private String asset;
    private double holding;

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public double getHolding() {
        return holding;
    }

    public void setHoldings(double holding) {
        this.holding = holding;
    }

    public String toString() {
        return getAsset() + ":\t" + getHolding();
    }
}