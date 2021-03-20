package org.sc.common.rest.assembler;

import org.sc.common.rest.PositionDto;
import org.sc.data.model.Position;

public class PositionDTOAssembler {

    private PositionDTOAssembler() { }

    public static PositionDto toDTO(Position input) {

        if(input == null){
            return null;
        }

        PositionDto output = new PositionDto();

        output.setName(input.getName());
        output.setTags(input.getTags());
        output.setMediaIds(input.getMediaIds());
        output.setCoordinates(TrailCoordinatesDTOAssembler.toDTO(input.getCoordinates()));

        return output;
    }
}
