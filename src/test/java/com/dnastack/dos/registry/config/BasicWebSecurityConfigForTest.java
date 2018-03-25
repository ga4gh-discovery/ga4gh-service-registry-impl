package com.dnastack.dos.registry.config;

import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This class servers as the web security configuration for testing with basic authenticaion
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
@Configuration
@Profile("it")
@EnableWebSecurity
@Order(99)
public class BasicWebSecurityConfigForTest extends WebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user")
                .password("password")
                .authorities("dos_user", "dos_owner");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("*")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic()
                .and()
                .addFilterAfter(authencationDecoratorFilter(), BasicAuthenticationFilter.class);;
    }

    @Bean
    AuthenticationDecoratorFilter authencationDecoratorFilter() {
        return new AuthenticationDecoratorFilter();
    }

    private static class AuthenticationDecoratorFilter extends GenericFilterBean {

        public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
                throws IOException, ServletException {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            org.springframework.util.Assert.isInstanceOf(
                    UsernamePasswordAuthenticationToken.class, authentication);

            String userName = ((User) ((UsernamePasswordAuthenticationToken) authentication).getPrincipal()).getUsername();

            ((UsernamePasswordAuthenticationToken) authentication).setDetails(getSimpleKeycloakAccount(userName.hashCode()+""));

            filterChain.doFilter(request, response);
        }

        public static SimpleKeycloakAccount getSimpleKeycloakAccount(String tokenId) {

            AccessToken accessToken = mock(AccessToken.class);
            when(accessToken.getId()).thenReturn(tokenId);

            RefreshableKeycloakSecurityContext keycloakSecurityContext = new RefreshableKeycloakSecurityContext(null, null, null, accessToken, null, null, null);
            SimpleKeycloakAccount simpleKeycloakAccount = new SimpleKeycloakAccount(null, null, keycloakSecurityContext);

            return simpleKeycloakAccount;
        }
    }
}
