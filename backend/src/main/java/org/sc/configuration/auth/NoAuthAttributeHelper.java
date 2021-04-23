package org.sc.configuration.auth;

import org.sc.configuration.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier(NoAuthAttributeHelper.NO_AUTH_BEAN)
public class NoAuthAttributeHelper implements AuthHelper {

    public static final String NO_AUTH_BEAN = "NO_AUTH_BEAN";

    @Autowired
    public NoAuthAttributeHelper(final AppProperties appProperties) { }

    public String getAttribute(UserAttribute userAttribute) {
        return "CAI-Bologna";
    }
}
