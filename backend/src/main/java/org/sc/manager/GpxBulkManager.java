package org.sc.manager;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import org.sc.data.model.*;
import org.sc.data.repository.TrailDatasetVersionDao;
import org.sc.manager.data.CreateGpxTrailsData;
import org.sc.manager.data.CreateGpxTrailsResult;
import org.sc.processor.GpxFileHandlerHelper;
import org.sc.processor.TrailsCalculator;
import org.sc.service.AltitudeServiceAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class GpxBulkManager {

    private GpxFileHandlerHelper gpxFileHandlerHelper;
    private AltitudeServiceAdapter altitudeServiceAdapter;
    private TrailsCalculator trailsCalculator;
    private TrailManager trailManager;
    private TrailDatasetVersionDao trailDatasetVersionDao;

    @Autowired
    public GpxBulkManager(GpxFileHandlerHelper gpxFileHandlerHelper, AltitudeServiceAdapter altitudeServiceAdapter,
                          TrailsCalculator trailsCalculator, TrailManager trailManager, TrailDatasetVersionDao trailDatasetVersionDao) {
        this.gpxFileHandlerHelper = gpxFileHandlerHelper;
        this.altitudeServiceAdapter = altitudeServiceAdapter;
        this.trailsCalculator = trailsCalculator;
        this.trailManager = trailManager;
        this.trailDatasetVersionDao = trailDatasetVersionDao;
    }

    public CreateGpxTrailsResult createTrailFromGpxBulkImport(CreateGpxTrailsData data) {

        CreateGpxTrailsResult result = new CreateGpxTrailsResult();

        Date now = new Date();

        data.getFilesGpxMap().entrySet().forEach(entry -> {

            Optional<Trail> optionalTrail = buildTrailFromFile(now + "_" + entry.getKey(), entry.getValue(), now);

            if(optionalTrail.isPresent()) {
                trailManager.save(optionalTrail.get());
                trailDatasetVersionDao.increaseVersion();

                result.getCreatedTrail().put(entry.getKey(), optionalTrail.get());

            } else {
                result.getCreatedTrail().put(entry.getKey(), null) ; //TODO: gestione non creazione
            }
        });

        return result;
    }

    private  Optional<Trail> buildTrailFromFile(String code, Path path, Date now) {

        try {
            GPX gpx = gpxFileHandlerHelper.readFromFile(path);
            Optional<Track> trackOptional = gpx.tracks().findFirst();

            if(trackOptional.isPresent()) {

                Track track = trackOptional.get();

                Optional<List<TrailCoordinates>> optionalCoordinates = buildTrailCoordinatesFromFile(track);

                if(optionalCoordinates.isPresent()) {

                    List<TrailCoordinates> coordinates = optionalCoordinates.get();

                    Trail trailData = Trail.builder()
                            .code(code)
                            .name(track.getName().map(value -> value).orElse(""))
                            .description(track.getDescription().map(value -> value).orElse(""))
                            .startPos(new Position(
                                    "",
                                    Collections.emptyList(),
                                    coordinates.get(0),
                                    Collections.emptyList()
                            ))
                            .finalPos(new Position(
                                    "",
                                    Collections.emptyList(),
                                    coordinates.get(coordinates.size()-1),
                                    Collections.emptyList()
                            ))
                            .coordinates(coordinates)
                            .statsTrailMetadata(new StatsTrailMetadata(
                                    trailsCalculator.calculateTotRise(coordinates),
                                    trailsCalculator.calculateTotFall(coordinates),
                                    trailsCalculator.calculateEta(coordinates),
                                    trailsCalculator.calculateTrailLength(coordinates)
                            ))
                            .lastUpdate(now)
                            .geoLineString(new GeoLineString(
                                            coordinates.stream().map(
                                                    elem -> new SimpleCoordinates(elem.getLongitude(), elem.getLatitude())
                                            ).collect(Collectors.toList())
                                    )
                            )
                            .locations(Collections.emptyList())
                            .mediaList(Collections.emptyList())
                            .build();

                    return Optional.of(trailData);
                }
            }
            //TODO: segnalare che problema c'è stato -> vedi validator in importTrail
            return Optional.empty();

        } catch(Exception e) {
            //TODO: logger
            return Optional.empty();
        }
    }

    private Optional<List<TrailCoordinates>> buildTrailCoordinatesFromFile(Track track) {

        Optional<TrackSegment> segmentOptional = track.segments().findFirst();

        if(segmentOptional.isPresent()) {

            List<CoordinatesWithAltitude> coordinatesList = new ArrayList<>();

            segmentOptional.get().points().forEach(wayPoint -> {
                CoordinatesWithAltitude pointCoordinate = new CoordinatesWithAltitude(
                        wayPoint.getLongitude().toDegrees(),
                        wayPoint.getLatitude().toDegrees(),
                        altitudeServiceAdapter.getAltitudeByLongLat(wayPoint.getLatitude().toDegrees(), wayPoint.getLongitude().toDegrees())
                );

                coordinatesList.add(pointCoordinate);
            });

            List<TrailCoordinates> trailCoordinatesList = coordinatesList.stream().map(elem ->
                    new TrailCoordinates(
                            elem.getLongitude(),
                            elem.getLatitude(),
                            elem.getAltitude(),
                            trailsCalculator.calculateLengthFromTo(coordinatesList, elem)
                    )
            ).collect(Collectors.toList());

            return Optional.of(trailCoordinatesList);
        }

        return Optional.empty();
    }

}
