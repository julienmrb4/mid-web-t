//package com.mhkcode.institution.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.security.config.Customizer;
//
//@Configuration
//@EnableWebSecurity
//public class WebSecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                // Disable CSRF for simplicity (enable it in production with proper handling)
//                .csrf(csrf -> csrf.disable())
//
//                // Configure authorization rules
//                .authorizeHttpRequests(authorize -> authorize
//                        // Public URLs that don't require authentication
//                        .requestMatchers(
//                                "/",
//                                "/login",
//                                "/register",
//                                "/dashboard",
//                                "/css/**",
//                                "/js/**",
//                                "/images/**"
//                        ).permitAll()
//                        // Secure role-specific dashboards
//                        .requestMatchers("/admin-dashboard/**").hasRole("ADMIN")
//                        .requestMatchers("/agent-dashboard/**").hasRole("AGENT")
//                        .requestMatchers("/inst-dashboard/**").hasRole("INSTITUTION")
//                        // All other URLs require authentication
//                        .anyRequest().authenticated()
//                )
//
//                // Custom form login configuration
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .loginProcessingUrl("/login")
//                        .usernameParameter("email")
//                        .passwordParameter("password")
//                        .permitAll()
//                        .disable()
//                )
//
//                // Logout configuration
//                .logout(logout -> logout
//                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                        .logoutSuccessUrl("/login?logout")
//                        .permitAll()
//                )
//
//                // Session management
//                .sessionManagement(session -> session
//                        .maximumSessions(1)
//                        .expiredUrl("/login?expired")
//                );
//
//        return http.build();
//    }
//}





package com.mhkcode.institution.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // Disable frame options to allow frames
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                )

                // Configure authorization
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/**").permitAll()
                )

                // Disable form login since you're handling it
                .formLogin(AbstractHttpConfigurer::disable)

                // Disable HTTP Basic
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}