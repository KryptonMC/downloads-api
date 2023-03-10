package org.kryptonmc.downloads.auth;

import org.kryptonmc.downloads.database.model.ApiToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.List;

public final class SimpleApiToken extends AbstractAuthenticationToken {

    private final ApiToken token;

    public SimpleApiToken(final ApiToken token) {
        super(List.of());
        this.token = token;
        setAuthenticated(true);
    }

    public ApiToken getToken() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token.token();
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public String getName() {
        return token.name();
    }
}
