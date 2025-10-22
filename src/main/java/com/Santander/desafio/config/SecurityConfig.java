package com.Santander.desafio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity 
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desabilita CSRF para API REST
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll() // Permite acesso ao console H2 (apenas para DEV) que acredito ser o caso
                .anyRequest().authenticated() 
            )
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
                // Configuração básica. O Spring precisa saber de onde validar o JWT.
                // Na prática, usariamos o `application.properties` para configurar o issuer-uri:
                // spring.security.oauth2.resourceserver.jwt.issuer-uri=https://seu-servidor-oauth/
                // Para testes, o token precisa ser assinado corretamente.
            }));

        return http.build();
    }
}
