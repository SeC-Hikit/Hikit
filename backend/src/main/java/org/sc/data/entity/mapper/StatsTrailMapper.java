package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.model.StatsTrailMetadata;
import org.springframework.stereotype.Component;

@Component
public class StatsTrailMapper implements Mapper<StatsTrailMetadata> {

    @Override
    public StatsTrailMetadata mapToObject(Document document) {
        return new StatsTrailMetadata(document.getDouble(StatsTrailMetadata.TOTAL_RISE),
                document.getDouble(StatsTrailMetadata.TOTAL_FALL),
                document.getDouble(StatsTrailMetadata.ETA),
                document.getDouble(StatsTrailMetadata.LENGTH),
                document.getDouble(StatsTrailMetadata.HIGHEST_PLACE),
                document.getDouble(StatsTrailMetadata.LOWEST_PLACE)
        );
    }

    @Override
    public Document mapToDocument(StatsTrailMetadata object) {
        return new Document(StatsTrailMetadata.TOTAL_RISE, object.getTotalRise())
                .append(StatsTrailMetadata.TOTAL_FALL, object.getTotalFall())
                .append(StatsTrailMetadata.LENGTH, object.getLength())
                .append(StatsTrailMetadata.ETA, object.getEta())
                .append(StatsTrailMetadata.HIGHEST_PLACE, object.getHighestPlace())
                .append(StatsTrailMetadata.LOWEST_PLACE, object.getLowestPlace());
    }
}
