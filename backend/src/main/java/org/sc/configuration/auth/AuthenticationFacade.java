package org.sc.configuration.auth;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


public class AuthenticationFacade implements AuthenticationProvider {

    @Override
    public KeycloakAuthenticationToken getAuth() {
        final HttpServletRequest request =
                ((ServletRequestAttributes)
                        Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return (KeycloakAuthenticationToken) request.getUserPrincipal();
    }
}
