package com.web;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.Product.Type;
import com.web.security.Role;
import com.web.security.RoleRepository;
import com.web.security.User;
import com.web.security.UserRepository;

import jakarta.transaction.Transactional;

@Configuration
@Profile("dev")
public class DataConfiguration {
	
	@Bean
	public CommandLineRunner dataLoader(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, ObjectMapper objectMapper) {
		return args -> {
			Role role = new Role();
			role.setName("ADMIN");
			roleRepository.save(role);
			User user = new User();
			user.setUsername("admin");
			user.setPassword(encoder.encode("12345"));
			user.addRole(role);
			userRepository.save(user);
	
		};
	}
}
