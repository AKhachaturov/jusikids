package com.web;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.Product.Type;

import jakarta.transaction.Transactional;

@Configuration
public class DataConfiguration {
	
	@Bean
	public CommandLineRunner dataLoader(ProductRepository repo, OAuthClientsRepository clientRep, PasswordEncoder encoder, ObjectMapper objectMapper) {
		return args -> {
			String json = new String(Files.readAllBytes(Paths.get("src/test/java/com/web/unit/user.json")));
			ProductRequest productRequest = objectMapper.readValue(json, ProductRequest.class);
			Product product = new Product(productRequest);
			repo.save(product);
			
			OAuthClient client = new OAuthClient();
			client.setClientId("admin");
			client.setClientSecret(encoder.encode("password"));
			client.setGrantTypes("client_credentials");
			client.setScopes("admin");
			client.setAccessTokenValidationSeconds(3600L);
			client.setRefreshTokenValidationSeconds(7200L);
			
			clientRep.save(client);
			
			
			
		};
	}
}
