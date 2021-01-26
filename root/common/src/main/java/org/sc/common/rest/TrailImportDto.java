package org.sc.common.rest;

import java.util.*;

public class TrailImportDto {
     private final String code;
     private final String name;
     private final String description;
     private final PositionDto startPos;
     private final PositionDto finalPos;
     private final List<PositionDto> locations;
     private final TrailClassification classification;
     private final String country;
     private final List<TrailCoordinatesDto> coordinates;
     private final Date lastUpdate;
     private final String maintainingSection;

     public TrailImportDto(String code, String name, String description, PositionDto startPos,
                           PositionDto finalPos, List<PositionDto> locations, TrailClassification classification,
                           String country, List<TrailCoordinatesDto> coordinates, Date lastUpdate, String maintainingSection) {
          this.code = code;
          this.name = name;
          this.description = description;
          this.startPos = startPos;
          this.finalPos = finalPos;
          this.locations = locations;
          this.classification = classification;
          this.country = country;
          this.coordinates = coordinates;
          this.lastUpdate = lastUpdate;
          this.maintainingSection = maintainingSection;
     }

     public String getCode() {
          return code;
     }

     public String getName() {
          return name;
     }

     public String getDescription() {
          return description;
     }

     public PositionDto getStartPos() {
          return startPos;
     }

     public PositionDto getFinalPos() {
          return finalPos;
     }

     public List<PositionDto> getLocations() {
          return locations;
     }

     public TrailClassification getClassification() {
          return classification;
     }

     public String getCountry() {
          return country;
     }

     public List<TrailCoordinatesDto> getCoordinates() {
          return coordinates;
     }

     public Date getLastUpdate() {
          return lastUpdate;
     }

     public String getMaintainingSection() {
          return maintainingSection;
     }
}