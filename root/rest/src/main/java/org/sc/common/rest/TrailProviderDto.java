package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TrailProviderDto {
    private String id;
    private String name;
    private String parentId;
    private String description;
    private boolean publicOrganization;
    private List<KeyValueDto> keyVal;
}
