package org.kryptonmc.downloads;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class DownloadsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DownloadsApiApplication.class, args);
    }
}
