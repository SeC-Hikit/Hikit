package org.sc.configuration.auth;

import org.apache.logging.log4j.Logger;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.IDToken;
import org.sc.configuration.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.apache.logging.log4j.LogManager.getLogger;

@Service
@Qualifier(KeycloakAuthAttributeHelper.KEYCLOAK_BEAN)
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

    public String getUsername(){
        final Object principal = authProvider.getAuth().getPrincipal();
        if(principal instanceof KeycloakPrincipal) {
            final KeycloakPrincipal<KeycloakSecurityContext> kp =
                    (KeycloakPrincipal<KeycloakSecurityContext>) principal;
            return kp.getKeycloakSecurityContext().getToken().getPreferredUsername();
        }
        throw new IllegalStateException();
    }

    public String getAttribute(UserAttribute userAttribute) {
        final Object principal = authProvider.getAuth().getPrincipal();
        if(principal instanceof KeycloakPrincipal) {
            final KeycloakPrincipal<KeycloakSecurityContext> kp =
                    (KeycloakPrincipal<KeycloakSecurityContext>) principal;
            final IDToken token = kp.getKeycloakSecurityContext().getToken();

            final Map<String, Object> claims = token.getOtherClaims();

            final String userAttributeName = userAttribute.name();
            if (claims.containsKey(userAttributeName)) {
                return String.valueOf(claims.get(userAttributeName));
            }
            LOGGER.warn(String.format("User '%s', does not contain claim '%s'", token.getName(), userAttributeName));
            return "";
        }
        LOGGER.error("principal {} not an instance of KeycloakPrincipal", principal);
        throw new IllegalStateException();
    }

    @Override
    public String getInstance() {
        return appProperties.getInstanceId();
    }

    @Override
    public String getRealm() {
        return getAttribute(UserAttribute.realm);
    }

}
