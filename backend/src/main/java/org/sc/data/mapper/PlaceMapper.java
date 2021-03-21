package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.CoordinatesDto;
import org.sc.common.rest.PlaceDto;
import org.sc.data.model.CoordinatesWithAltitude;
import org.sc.data.model.MultiPointCoords2D;
import org.sc.data.model.Place;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PlaceMapper {

    default Place map(PlaceDto placeDto) {
        final MultiPointCoords2D multiPointCoords2D = new MultiPointCoords2D(placeDto.getCoordinates()
                .stream()
                .map(coordinates ->
                        Arrays.asList(coordinates.getLongitude(),
                                coordinates.getLatitude()))
                .collect(Collectors.toList()));

        List<CoordinatesWithAltitude> coordinates = placeDto.getCoordinates().stream().map(c ->
                new CoordinatesWithAltitude(c.getLatitude(), c.getLongitude(), c.getAltitude())
        ).collect(Collectors.toList());

        return new Place(placeDto.getId(), placeDto.getName(), placeDto.getDescription(),
                placeDto.getTags(), placeDto.getMediaIds(), multiPointCoords2D,
                coordinates, placeDto.getCrossingTrailIds());
    }

    default PlaceDto map(Place place) {
        List<CoordinatesDto> coordinatesWithAltitude = place.getCoordinates().stream().map(
                c -> new CoordinatesDto(c.getLatitude(), c.getLongitude(), c.getAltitude())
        ).collect(Collectors.toList());
        return new PlaceDto(place.getId(), place.getName(), place.getDescription(),
                place.getTags(), place.getMediaIds(), coordinatesWithAltitude, place.getCrossingTrailIds());
    }
}
