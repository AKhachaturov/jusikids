package com.web;

import jakarta.persistence.Table;

import org.springframework.context.annotation.Profile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "clients")
@NoArgsConstructor
@Profile("test")
public class OAuthClient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true, nullable = false)
	private String clientId;
	
	@Column(nullable = false)
	private String clientSecret;
	
	@Column(nullable = false)
	private String grantTypes;

	@Column(nullable = false)
	private String scopes;
	
	@Column(nullable = true)
	private String redirectUris;
	
	@Column(nullable = false)
	private Long accessTokenValidationSeconds;
	
	@Column(nullable = false)
	private Long refreshTokenValidationSeconds;
	
}
