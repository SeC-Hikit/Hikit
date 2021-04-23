package org.sc.configuration.auth;

import org.sc.configuration.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AuthFacade {

    private final AuthHelper keycloakHelper;
    private final AppProperties appProperties;
    private final AuthHelper notAuthHelper;

    @Autowired
    public AuthFacade(final AppProperties appProperties,
                      final @Qualifier(NoAuthAttributeHelper.NO_AUTH_BEAN) AuthHelper notAuthHelper,
                      final @Qualifier(KeycloakAuthAttributeHelper.KEYCLOAK_BEAN) AuthHelper keycloakHelper) {
        this.appProperties = appProperties;
        this.notAuthHelper = notAuthHelper;
        this.keycloakHelper = keycloakHelper;
    }

    public AuthHelper getNotAuthHelper() {
        if(false){
            return notAuthHelper;
        }
        return keycloakHelper;
    }
}
