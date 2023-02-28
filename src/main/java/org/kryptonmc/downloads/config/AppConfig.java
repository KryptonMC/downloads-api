package org.kryptonmc.downloads.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "app")
public record AppConfig(Path storagePath) {
}
