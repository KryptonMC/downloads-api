package org.kryptonmc.downloads.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final AuthenticationEntryPoint entryPoint;
    private final TokenAuthProvider authProvider;

    @Autowired
    public SecurityConfiguration(final AuthenticationEntryPoint entryPoint, final TokenAuthProvider authProvider) {
        this.entryPoint = entryPoint;
        this.authProvider = authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http
            // Disable default configurations
            .logout().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .sessionManagement().disable()
            .csrf().disable()
            // Add custom auth filter
            .addFilterBefore(
                new TokenAuthFilter(new AntPathRequestMatcher("/downloads/v1/upload"), authenticationManager(), entryPoint),
                AnonymousAuthenticationFilter.class
            )
            .authorizeHttpRequests().anyRequest().permitAll();
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authProvider);
    }
}
