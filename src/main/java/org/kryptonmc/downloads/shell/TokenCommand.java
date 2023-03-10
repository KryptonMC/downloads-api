package org.kryptonmc.downloads.shell;

import org.apache.commons.codec.binary.Base16;
import org.bson.types.ObjectId;
import org.kryptonmc.downloads.database.model.ApiToken;
import org.kryptonmc.downloads.database.repository.ApiTokenCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@ShellComponent
@ShellCommandGroup("Token Commands")
public class TokenCommand {

    private static final Base16 ENCODER = new Base16(true);

    private final ApiTokenCollection tokens;

    @Autowired
    public TokenCommand(final ApiTokenCollection tokens) {
        this.tokens = tokens;
    }

    @ShellMethod(key = "token create", value = "Creates a new API token")
    public String create(final String name) {
        if (tokens.existsByName(name)) return "A token with that name already exists!";
        final String tokenValue = generateToken();
        final ApiToken token = new ApiToken(new ObjectId(), name, tokenValue);
        tokens.save(token);
        return "Created token '" + name + "' with value " + tokenValue;
    }

    @ShellMethod(key = "token regenerate", value = "Regenerates an existing API token")
    public String regenerate(final String name) {
        final ApiToken token = tokens.findByName(name).orElse(null);
        if (token == null) return "No token with name '" + name + "' exists!";
        final String tokenValue = generateToken();
        final ApiToken newToken = new ApiToken(token._id(), token.name(), tokenValue);
        tokens.save(newToken);
        return "Regenerated token '" + name + "'";
    }

    @ShellMethod(key = "token delete", value = "Deletes an API token")
    public String delete(final String name) {
        final ApiToken token = tokens.findByName(name).orElse(null);
        if (token == null) return "No token with name '" + name + "' exists!";
        tokens.delete(token);
        return "Deleted token '" + name + "'";
    }

    @ShellMethod(key = "token list", value = "Lists all API token names")
    public String list() {
        final List<ApiToken> tokens = this.tokens.findAll();
        if (tokens.isEmpty()) return "No tokens found!";

        final StringBuilder result = new StringBuilder("Tokens:");
        result.append('\n');

        for (int i = 0; i < tokens.size(); i++) {
            result.append("  - ");
            result.append(tokens.get(i).name());
            if (i < tokens.size() - 1) result.append('\n');
        }
        return result.toString();
    }

    private static String generateToken() {
        final var random = ThreadLocalRandom.current();
        final byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return ENCODER.encodeToString(bytes);
    }
}
