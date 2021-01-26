package org.sc.common.rest;

public class StatsTrailMetadataDto {
    private double totalRise;
    private double totalFall;
    private double eta;
    private double length;

    public StatsTrailMetadataDto() { }

    public StatsTrailMetadataDto(double totalRise, double totalFall,
                          double eta, double length) {
        this.totalRise = totalRise;
        this.totalFall = totalFall;
        this.eta = eta;
        this.length = length;
    }

    public double getTotalRise() {
        return totalRise;
    }

    public double getTotalFall() {
        return totalFall;
    }

    public double getEta() {
        return eta;
    }

    public double getLength() {
        return length;
    }

    public void setTotalRise(double totalRise) {
        this.totalRise = totalRise;
    }

    public void setTotalFall(double totalFall) {
        this.totalFall = totalFall;
    }

    public void setEta(double eta) {
        this.eta = eta;
    }

    public void setLength(double length) {
        this.length = length;
    }
}