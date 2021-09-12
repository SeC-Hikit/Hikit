package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.AccessibilityReportDto;
import org.sc.data.model.AccessibilityReport;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AccessibilityReportMapper {
    AccessibilityReportDto map(AccessibilityReport ar);

    @Mapping(target = "valid", ignore = true)
    AccessibilityReport map(AccessibilityReportDto ar);
}
