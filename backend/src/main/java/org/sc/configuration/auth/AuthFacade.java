package org.sc.configuration.auth;

import org.sc.configuration.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AuthFacade {

    final static Logger LOGGER = LoggerFactory.getLogger(AuthFacade.class);

    private final AuthHelper keycloakHelper;
    private final AppProperties appProperties;
    private final AuthHelper noAuthHelper;

    @Autowired
    public AuthFacade(final AppProperties appProperties,
                      final @Qualifier(NoAuthAttributeHelper.NO_AUTH_BEAN) AuthHelper noAuthHelper,
                      final @Qualifier(KeycloakAuthAttributeHelper.KEYCLOAK_BEAN) AuthHelper keycloakHelper) {
        this.appProperties = appProperties;
        this.noAuthHelper = noAuthHelper;
        this.keycloakHelper = keycloakHelper;
        if(!appProperties.getIsSecurityEnabled()){
            LOGGER.warn("Security is disabled - check the conf to see user/attributes");
        }
    }

    public AuthHelper getAuthHelper() {
        if(!appProperties.getIsSecurityEnabled()){
            return noAuthHelper;
        }
        return keycloakHelper;
    }
}
