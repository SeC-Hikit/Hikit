package org.sc.configuration;

import org.apache.logging.log4j.Logger;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.management.HttpSessionManager;
import org.sc.configuration.auth.AuthenticationFacade;
import org.sc.configuration.auth.AuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import java.io.InputStream;

import static org.apache.logging.log4j.LogManager.getLogger;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(value = "security.enabled", havingValue = "false")
public class NoSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = getLogger(NoSecurityConfig.class);

    public static final String ALL_ALLOWED_WILDCARD = "/**";
    public static final String AUTHENTICATION_IS_DISABLED_MESSAGE = "Authentication is disabled";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        LOGGER.warn(AUTHENTICATION_IS_DISABLED_MESSAGE);
        http.csrf().disable().authorizeRequests().anyRequest().permitAll();
    }

}