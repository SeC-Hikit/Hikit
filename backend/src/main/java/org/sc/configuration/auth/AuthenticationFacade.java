package org.sc.configuration.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class AuthenticationFacade implements AuthenticationProvider{
    @Override
    public Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
