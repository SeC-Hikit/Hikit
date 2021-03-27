package org.sc.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KeyValueDto {
    private String key;
    private String value;
}
