package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class KeyVal {

    public final static String KEY = "key";
    public final static String VAL = "val";

    private String key;
    private String value;
}
