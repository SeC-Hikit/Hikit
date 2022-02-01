package org.sc.configuration;

import org.apache.logging.log4j.Logger;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.management.HttpSessionManager;
import org.sc.configuration.auth.AuthenticationFacade;
import org.sc.configuration.auth.AuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import static org.apache.logging.log4j.LogManager.getLogger;

@KeycloakConfiguration
@ConditionalOnProperty(value = "security.enabled", havingValue = "true")
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    private static final Logger LOGGER = getLogger(SecurityConfig.class);

    public static final String ALL_ALLOWED_WILDCARD = "/**";
    public static final String PROTECTED_CONTEXT = "/admin/**";

    final AppProperties appProperties;

    @Autowired
    public SecurityConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    /**
     * Registers the KeycloakAuthenticationProvider with the authentication manager.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        SimpleAuthorityMapper grantedAuthorityMapper = new SimpleAuthorityMapper();
        grantedAuthorityMapper.setPrefix("ROLE_");
        auth.authenticationProvider(keycloakAuthenticationProvider());
    }

    /**
     * Defines the session authentication strategy.
     */
    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    protected AuthenticationProvider authenticationProvider() {
        return new AuthenticationFacade();
    }

    @Bean
    @Override
    @ConditionalOnMissingBean(HttpSessionManager.class)
    protected HttpSessionManager httpSessionManager() {
        return new HttpSessionManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.csrf().disable().cors().disable().authorizeRequests()
                .antMatchers(PROTECTED_CONTEXT).hasAuthority("admin")
                .antMatchers(ALL_ALLOWED_WILDCARD).permitAll()
                .anyRequest().authenticated();
    }

    @Bean
    public KeycloakConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }
}