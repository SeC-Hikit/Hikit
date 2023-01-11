package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.AnnouncementDto;
import org.sc.data.model.Announcement;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AnnouncementMapper {
    AnnouncementDto map(Announcement an);

    @Mapping(source = "id", target = "id")
    Announcement map(AnnouncementDto an);
}
