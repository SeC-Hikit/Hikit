package org.sc.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class User {
    private String id;
    private String username;
    private String email;
    private String password;
    private String description;
    private List<String> organizationIds;
}
