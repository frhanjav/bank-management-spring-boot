package com.example.bankmanagement.security;

import lombok.RequiredArgsConstructor; // Ensure you have this
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler; // Import AccessDeniedHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Needed for @PreAuthorize
@RequiredArgsConstructor // Use constructor injection
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final AccessDeniedHandler customAccessDeniedHandler; // Inject the custom handler

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for simplicity, enable in production with proper handling
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/error", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/manager/**").hasRole("MANAGER")
                        .requestMatchers("/staff/**").hasRole("STAFF")
                        .requestMatchers("/customer/**").hasRole("CUSTOMER")
                        .requestMatchers("/dashboard").authenticated() // Any logged-in user can see the dashboard dispatcher
                        .anyRequest().authenticated() // All other requests need authentication
                )
                .formLogin(form -> form
                        .loginPage("/login") // Custom login page URL
                        .loginProcessingUrl("/login") // URL to submit the username and password to
                        .defaultSuccessUrl("/dashboard", true) // Redirect to dashboard on success
                        .failureUrl("/login?error=true")  // Redirect on failure
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // URL to trigger logout
                        .logoutSuccessUrl("/login?logout=true")  // Redirect after logout
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                // ---> ADD EXCEPTION HANDLING CONFIGURATION <---
                .exceptionHandling(exceptions -> exceptions
                                .accessDeniedHandler(customAccessDeniedHandler) // Use custom handler for 403
                        // You could also configure authenticationEntryPoint for unauthenticated users
                        // but formLogin().loginPage() already handles the redirect to /login
                        // .authenticationEntryPoint(...)
                )
                .authenticationProvider(authenticationProvider()); // Register the custom provider

        return http.build();
    }
}