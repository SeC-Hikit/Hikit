package org.sc.data.entity.mapper;

import org.bson.Document;
import org.sc.data.model.StatsTrailMetadata;

public class TrailMapperUtil {
    public static StatsTrailMetadata getMetadata(final Document doc) {
        return new StatsTrailMetadata(doc.getDouble(StatsTrailMetadata.TOTAL_RISE),
                doc.getDouble(StatsTrailMetadata.TOTAL_FALL),
                doc.getDouble(StatsTrailMetadata.ETA),
                doc.getDouble(StatsTrailMetadata.LENGTH),
                doc.getDouble(StatsTrailMetadata.HIGHEST_PLACE),
                doc.getDouble(StatsTrailMetadata.LOWEST_PLACE));
    }

}
