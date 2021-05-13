package org.sc.configuration.auth;

public interface AuthHelper {
    String getUsername();
    String getAttribute(UserAttribute attribute);
    String getInstance();
    String getRealm();
}
