package org.sc.data.auth;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class UserToAttributes {
    private String username;
    private Map<String, String> attributes;

    public UserToAttributes() {
        this.username = "";
        this.attributes = new HashMap<>();
    }
}
