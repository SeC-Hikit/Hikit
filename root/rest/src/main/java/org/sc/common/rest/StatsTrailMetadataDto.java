package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatsTrailMetadataDto {
    private double totalRise;
    private double totalFall;
    private double eta;
    private double length;
    private double highestPlace;
    private double lowestPlace;
}