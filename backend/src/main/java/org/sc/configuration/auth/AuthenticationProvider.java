package org.sc.configuration.auth;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;

public interface AuthenticationProvider {
    KeycloakAuthenticationToken getAuth();
}
