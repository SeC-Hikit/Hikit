package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StatsTrailMetadata {

    public static final String TOTAL_RISE = "totalRise";
    public static final String TOTAL_FALL = "totalFall";
    public static final String ETA = "totalEta";
    public static final String LENGTH = "length";
    public static final String HIGHEST_PLACE = "highest";
    public static final String LOWEST_PLACE = "lowest";

    private double totalRise;
    private double totalFall;
    private double eta;
    private double length;
    private double highestPlace;
    private double lowestPlace;
}
