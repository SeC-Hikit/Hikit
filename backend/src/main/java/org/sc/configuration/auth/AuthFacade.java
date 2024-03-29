package org.sc.configuration.auth;

import org.sc.configuration.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthFacade {

    final static Logger LOGGER = LoggerFactory.getLogger(AuthFacade.class);
    private final AuthHelper authHelper;

    @Autowired
    public AuthFacade(final AppProperties appProperties,
                      final AuthHelper authHelper) {
        this.authHelper = authHelper;
        if(!appProperties.getIsSecurityEnabled()){
            LOGGER.warn("Security is disabled - check the conf to see user/attributes");
        }
    }

    public AuthHelper getAuthHelper() {
            return authHelper;
    }
}
