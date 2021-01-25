package org.sc.data.entity;

public class StatsTrailMetadata {

    public static final String TOTAL_RISE = "totalRise";
    public static final String TOTAL_FALL = "totalFall";
    public static final String ETA = "totalEta";
    public static final String LENGTH = "length";

    private final double totalRise;
    private final double totalFall;
    private final double eta;
    private final double length;

    public StatsTrailMetadata(double totalRise, double totalFall, double eta, double length) {
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
}
