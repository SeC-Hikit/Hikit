package org.sc.common.rest.assembler;

import org.sc.common.rest.TrailCoordinatesDto;
import org.sc.data.model.TrailCoordinates;

import java.util.ArrayList;
import java.util.List;

public class TrailCoordinatesDTOAssembler {

    private TrailCoordinatesDTOAssembler() { }

    public static TrailCoordinatesDto toDTO(TrailCoordinates input) {

        if(input == null) {
            return null;
        }

        TrailCoordinatesDto output = new TrailCoordinatesDto();

        output.setAltitude(input.getAltitude());
        output.setLatitude(input.getLatitude());
        output.setLongitude(input.getLongitude());
        output.setDistanceFromTrailStart(input.getDistanceFromTrailStart());

        return output;
    }

    public static List<TrailCoordinatesDto> toDTOList(List<TrailCoordinates> input) {
        List<TrailCoordinatesDto> output = new ArrayList<>();

        if(input != null) {

            input.forEach(elem -> {
                TrailCoordinatesDto dtoElem = toDTO(elem);

                if(dtoElem != null) {
                    output.add(dtoElem);
                }
            });
        }

        return output;
    }
}
