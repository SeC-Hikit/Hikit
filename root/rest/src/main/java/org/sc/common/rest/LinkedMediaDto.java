package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
public class LinkedMediaDto {
    private String id;
    private String description;
    private List<KeyValueDto> keyVal;
}
