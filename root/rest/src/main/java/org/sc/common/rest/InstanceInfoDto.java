package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class InstanceInfoDto {
    private String realm;
    private String instance;
    private String runningVersion;
}
