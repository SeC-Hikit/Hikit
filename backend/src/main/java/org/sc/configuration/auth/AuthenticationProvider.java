package org.sc.configuration.auth;

import org.springframework.security.core.Authentication;

public interface AuthenticationProvider {
    Authentication getAuth();

}
