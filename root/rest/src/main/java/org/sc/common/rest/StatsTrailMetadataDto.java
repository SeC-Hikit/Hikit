package org.sc.common.rest;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatsTrailMetadataDto that = (StatsTrailMetadataDto) o;
        return Double.compare(that.getTotalRise(), getTotalRise()) == 0 &&
                Double.compare(that.getTotalFall(), getTotalFall()) == 0 &&
                Double.compare(that.getEta(), getEta()) == 0 &&
                Double.compare(that.getLength(), getLength()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTotalRise(), getTotalFall(), getEta(), getLength());
    }
}