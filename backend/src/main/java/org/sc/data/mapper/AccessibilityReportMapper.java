package org.sc.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.sc.common.rest.AccessibilityReportDto;
import org.sc.data.model.AccessibilityReport;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AccessibilityReportMapper {
    AccessibilityReportDto map(AccessibilityReport ar);

    AccessibilityReport map(AccessibilityReportDto ar);
}
