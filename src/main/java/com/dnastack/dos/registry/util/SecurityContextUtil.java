package com.dnastack.dos.registry.util;

import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextUtil {

    public static String getUserId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //NOTE: it assumes a Keycloak tight coupling here.
        String id = ((SimpleKeycloakAccount) authentication.getDetails())
                .getKeycloakSecurityContext().getToken().getId();

        return id;

    }
}
