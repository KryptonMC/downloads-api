package org.kryptonmc.downloads.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;

public final class TokenAuthFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenAuthFilter.class);

    public TokenAuthFilter(final RequestMatcher requiresAuth, final AuthenticationManager manager, final AuthenticationEntryPoint entryPoint) {
        super(requiresAuth);
        setAuthenticationManager(manager);
        setAuthenticationFailureHandler(new AuthenticationEntryPointFailureHandler(entryPoint));
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) throws AuthenticationException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Token ")) {
            throw new BadCredentialsException("None or invalid API token header!");
        }
        final TokenAuthRequest authRequest = new TokenAuthRequest(authHeader.substring(6));
        return getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
                                            final FilterChain chain, final Authentication authResult) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        LOGGER.debug("Set SecurityContextHolder to {}", authResult);
        chain.doFilter(request, response);
    }
}
