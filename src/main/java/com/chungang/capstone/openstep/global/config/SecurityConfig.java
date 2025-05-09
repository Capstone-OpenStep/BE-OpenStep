package com.chungang.capstone.openstep.global.config;


import com.chungang.capstone.openstep.global.security.filter.JwtRequestFilter;
import com.chungang.capstone.openstep.global.security.principal.PrincipalDetailsService;
import com.chungang.capstone.openstep.global.security.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final PrincipalDetailsService principalDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                //.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .httpBasic(httpBasic -> httpBasic.disable())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        authorize -> authorize
                                // Member 관련 접근
                                .requestMatchers("/member/register", "/member/login/kakao", "/member/login/naver", "/member/login/email","/member/refresh").permitAll()
                                .requestMatchers("/github/auth/**").permitAll()

                                // Repo 관련 접근
                                .requestMatchers("/repo/trending", "/repo/{repo-id}", "/summary/repo/{repo-id}").permitAll()
                                .requestMatchers("/bookmark/add/{member-id}/{repo-id}", "/bookmark/delete/{member-id}/{repo-id}", "/bookmark/list/{member-id}").permitAll()
                                .requestMatchers("/repo/search/name").permitAll()

                                // Issue 관련 접근
                                .requestMatchers("/issues/trending", "/issues/{issue-id}", "/summary/issue/{issue-id}").permitAll()

                                // 기타 관련 접근
                                .requestMatchers("/example/**").permitAll()
                                .requestMatchers("/", "/api-docs/**", "/api-docs/swagger-config/*", "/swagger-ui/*", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtRequestFilter(jwtTokenProvider, principalDetailsService), UsernamePasswordAuthenticationFilter.class)

                .build();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        //config.setAllowedOriginPatterns(List.of("*", "http://localhost:3000"));
        config.setAllowedOriginPatterns(List.of("*"));
        //config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
