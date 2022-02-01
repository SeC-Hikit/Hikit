package org.sc.configuration.auth;

import org.apache.logging.log4j.Logger;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.sc.configuration.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.apache.logging.log4j.LogManager.getLogger;

@Service
@ConditionalOnProperty(value = "security.enabled", havingValue = "true")
public class KeycloakAuthAttributeHelper implements AuthHelper {
    private static final Logger LOGGER = getLogger(KeycloakAuthAttributeHelper.class);

    public static final String KEYCLOAK_BEAN = "KEYCLOAK_BEAN";

    private final AuthenticationProvider authProvider;
    private final AppProperties appProperties;

    @Autowired
    public KeycloakAuthAttributeHelper(final AuthenticationProvider authenticationProvider,
                                       final AppProperties appProperties) {
        this.authProvider = authenticationProvider;
        this.appProperties = appProperties;
    }

    public String getUsername() {
        final KeycloakAuthenticationToken auth = authProvider.getAuth();
        final AccessToken token = auth.getAccount().getKeycloakSecurityContext().getToken();
        return token.getPreferredUsername();
    }

    public String getAttribute(final UserAttribute userAttribute) {
        final KeycloakAuthenticationToken auth = authProvider.getAuth();
        final AccessToken token = auth.getAccount().getKeycloakSecurityContext().getToken();
        final Map<String, Object> claims = token.getOtherClaims();
        final String userAttributeName = userAttribute.name();
        if (claims.containsKey(userAttributeName)) {
            return String.valueOf(claims.get(userAttributeName));
        }
        throw new SecurityException(
                String.format("User '%s', does not contain claim '%s'", token.getName(), userAttributeName
                )
        );
    }

    @Override
    public String getInstance() {
        return appProperties.getInstanceId();
    }

    @Override
    public String getRealm() {
        return getAttribute(UserAttribute.realm);
    }

    @Override
    public AuthData getAuthData() {
        return new AuthData(getUsername(), getRealm(), getInstance());
    }

}
