package org.sc.common.rest.assembler;

import org.sc.common.rest.TrailPreparationModelDto;
import org.sc.data.model.Trail;

import java.util.ArrayList;
import java.util.List;

public class TrailDTOAssembler {

    private TrailDTOAssembler() { }

    public static TrailPreparationModelDto toPreparationModelDTO(Trail input) {

        if(input == null) {
            return null;
        }

        TrailPreparationModelDto output = new TrailPreparationModelDto();

        output.setCoordinates(TrailCoordinatesDTOAssembler.toDTOList(input.getCoordinates()));
        output.setDescription(input.getDescription());
        output.setFinalPos(PositionDTOAssembler.toDTO(input.getFinalPos()));
        output.setStartPos(PositionDTOAssembler.toDTO(input.getStartPos()));
        output.setName(input.getName());

        return output;
    }

    public static List<TrailPreparationModelDto> toPreparationModelDTOList(List<Trail> input) {

        List<TrailPreparationModelDto> output = new ArrayList<TrailPreparationModelDto>();

        if(input != null) {

            input.forEach(elem -> {

                TrailPreparationModelDto elemDTO = toPreparationModelDTO(elem);

                if(elemDTO != null) {
                    output.add(elemDTO);
                }
            });
        }

        return output;
    }
}
