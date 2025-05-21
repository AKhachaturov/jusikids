package com.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@Profile("test")
public class SecurityConfig {
	
	@SuppressWarnings({ "removal" })
	@Bean
	@Order(2)
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
		.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.DELETE, "api/**").hasAuthority("SCOPE_admin")
				.requestMatchers(HttpMethod.POST, "api/products/**").hasAuthority("SCOPE_admin")
				.requestMatchers(HttpMethod.PATCH, "api/products/**").hasAuthority("SCOPE_admin")
				.requestMatchers(HttpMethod.GET).permitAll()
				.anyRequest().hasAuthority("SCOPE_admin")
				)
		.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
		.csrf(csrf -> csrf.disable());
		
		return http.build();
	}
	
	@Bean
	public JwtDecoder jwkDecoder() {
		return NimbusJwtDecoder.withJwkSetUri("http://localhost:8080/oauth2/jwks").build();
	}
}
