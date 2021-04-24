package org.sc.configuration.auth;

import org.apache.logging.log4j.Logger;
import org.sc.configuration.AppProperties;
import org.sc.data.auth.UserToAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.logging.log4j.LogManager.getLogger;

@Service
@Qualifier(NoAuthAttributeHelper.NO_AUTH_BEAN)
public class NoAuthAttributeHelper implements AuthHelper {

    public static final String NO_AUTH_BEAN = "NO_AUTH_BEAN";

    public static final String USER_DIVIDER = ";";
    public static final String USER_ATTRIBUTES_DIVIDER = "/";
    public static final String ATTRIBUTE_RECOGNIZER = "=";

    public static final int KEY_PAIR_SIZE = 2;
    public static final int ELEM_ZERO = 0;
    public static final int ELEM_ONE = 1;

    final UserToAttributes userToAttributes;

    @Autowired
    public NoAuthAttributeHelper(final AppProperties appProperties) {
        userToAttributes = constructUserToAttributes(appProperties);
    }

    @Override
    public String getUsername() {
        return userToAttributes.getUsername();
    }

    public String getAttribute(UserAttribute userAttribute) {
        return userToAttributes.getAttributes().getOrDefault(userAttribute.name(), "");
    }

    private UserToAttributes constructUserToAttributes(AppProperties appProperties) {
        final String securityDisabledUserRoles = appProperties.getSecurityDisabledUserRoles();
        final String[] userAndRoles = securityDisabledUserRoles.split(USER_DIVIDER);
        final List<String> collect = Arrays.stream(userAndRoles)
                .map(a -> a.split(USER_ATTRIBUTES_DIVIDER))
                .flatMap(Stream::of)
                .collect(Collectors.toList());

        final UserToAttributes userToAttributes = new UserToAttributes();

        collect.forEach(a -> {
            if (a.contains(ATTRIBUTE_RECOGNIZER)) {
                final List<String> split = Arrays.asList(a.split(ATTRIBUTE_RECOGNIZER));
                if (split.size() != KEY_PAIR_SIZE) {
                    throw new IllegalArgumentException();
                }
                userToAttributes.getAttributes().put(
                        split.get(ELEM_ZERO),
                        split.get(ELEM_ONE));
            } else {
                userToAttributes.setUsername(a);
            }
        });
        return userToAttributes;
    }
}
