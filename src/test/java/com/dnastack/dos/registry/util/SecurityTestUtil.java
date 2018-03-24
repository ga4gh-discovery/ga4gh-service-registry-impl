package com.dnastack.dos.registry.util;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

/**
 * This class servers as ...
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
public class SecurityTestUtil {
    public static RequestPostProcessor authDosUser() {
        return user("user")
                .password("password")
                .authorities(new SimpleGrantedAuthority("dos_user"));
    }

    public static RequestPostProcessor authDosOwner() {
        return user("user")
                .password("password")
                .authorities(new SimpleGrantedAuthority("dos_owner"));
    }

    public static RequestPostProcessor authRW() {
        return user("user")
                .password("password")
                .authorities(new SimpleGrantedAuthority("dos_owner"), new SimpleGrantedAuthority("dos_user"));
    }
}
