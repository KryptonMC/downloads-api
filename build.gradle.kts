plugins {
    java
    id("org.springframework.boot") version "3.0.3"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "org.kryptonmc"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring
    implementation("org.springframework.boot", "spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot", "spring-boot-starter-web")
    implementation("org.springframework.boot", "spring-boot-starter-security")
    implementation("org.springframework.shell", "spring-shell-starter", "3.0.1")
    annotationProcessor("org.springframework.boot", "spring-boot-configuration-processor")

    // Other stuff
    implementation("commons-codec", "commons-codec", "1.15")
    implementation("org.springdoc", "springdoc-openapi-starter-webmvc-ui", "2.0.2")
}

tasks.bootJar {
    archiveFileName.set("DownloadsAPI-${project.version}.jar")
}
