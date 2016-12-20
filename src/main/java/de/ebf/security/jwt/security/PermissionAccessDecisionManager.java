package de.ebf.security.jwt.security;

import java.util.Collection;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;

import de.ebf.security.jwt.authentication.JWTAuthentication;

public class PermissionAccessDecisionManager implements AccessDecisionManager {

    private static final Logger logger = LoggerFactory.getLogger(PermissionAccessDecisionManager.class);

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
            throws AccessDeniedException, InsufficientAuthenticationException {

        if (!JWTAuthentication.class.isAssignableFrom(authentication.getClass())) {
            throw new InsufficientAuthenticationException("Authentication object unknown.");
        }

        Stream<ConfigAttribute> accessableAttributes = configAttributes.stream().filter(configAttr -> {
            return authentication.getAuthorities().stream().filter(authority -> {
                return authority.getAuthority().equals(configAttr.getAttribute());
            }).count() == 1;
        });

        if (accessableAttributes.count() == 0) {
            throw new AccessDeniedException("Insuficient permissions.");
        }

    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return ProtectedResourceSecurityAttribute.class.isAssignableFrom(attribute.getClass());
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

}
