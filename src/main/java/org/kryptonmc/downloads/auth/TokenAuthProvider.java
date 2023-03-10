package org.kryptonmc.downloads.auth;

import org.kryptonmc.downloads.database.model.ApiToken;
import org.kryptonmc.downloads.database.repository.ApiTokenCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class TokenAuthProvider implements AuthenticationProvider {

    private final ApiTokenCollection tokens;

    @Autowired
    public TokenAuthProvider(final ApiTokenCollection tokens) {
        this.tokens = tokens;
    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final TokenAuthRequest request = (TokenAuthRequest) authentication;
        final ApiToken storedToken = tokens.findByToken(request.token)
            .orElseThrow(() -> new BadCredentialsException("Unable to find API token!"));
        return new SimpleApiToken(storedToken);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return TokenAuthRequest.class.isAssignableFrom(authentication);
    }
}
