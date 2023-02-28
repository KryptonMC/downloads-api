package org.kryptonmc.downloads.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI openAPI() {
        final OpenAPI api = new OpenAPI();
        api.info(new Info()
            .title("Krypton Downloads API")
            .description("Download artifacts for Krypton and related projects"));
        api.servers(List.of(new Server().url("https://api.kryptonmc.org/downloads")));
        return api;
    }
}
