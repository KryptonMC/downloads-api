package org.kryptonmc.downloads.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.List;

public final class TokenAuthRequest extends AbstractAuthenticationToken {

    final String token;

    public TokenAuthRequest(final String token) {
        super(List.of());
        this.token = token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        throw new UnsupportedOperationException("Cannot get principal from authentication request!");
    }
}
