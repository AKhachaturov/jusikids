package com.web;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OAuthClientService implements RegisteredClientRepository {

	private final OAuthClientsRepository clientRep;
	private final PasswordEncoder encoder;
	
	public OAuthClientService(OAuthClientsRepository rep, PasswordEncoder encoder) {
		clientRep = rep;
		this.encoder = encoder;
	}
	
	@Override
	public RegisteredClient findById(String id) {
		return clientRep.findById(Long.parseLong(id)).map(this::toRegisteredClient).orElse(null);
	}
	
	@Override
	public RegisteredClient findByClientId(String clientId) {
		return clientRep.findByClientId(clientId).map(this::toRegisteredClient).orElse(null);
	}

	@Override
	public void save(RegisteredClient regClient) {
		Optional<OAuthClient> existingClient = clientRep.findByClientId(regClient.getClientId());
		OAuthClient client = existingClient.orElse(new OAuthClient());
		
		client.setClientId(regClient.getClientId());
		client.setClientSecret(regClient.getClientSecret());
		client.setScopes(String.join(",", regClient.getScopes()));
		client.setGrantTypes(regClient.getAuthorizationGrantTypes()
				.stream().map(AuthorizationGrantType::getValue).collect(Collectors.joining(",")));
		client.setRedirectUris(String.join(",", regClient.getRedirectUris()));
		client.setAccessTokenValidationSeconds(regClient.getTokenSettings().getAccessTokenTimeToLive().toSeconds());
		client.setRefreshTokenValidationSeconds(regClient.getTokenSettings().getRefreshTokenTimeToLive().toSeconds());
		
		clientRep.save(client);
	}
	
	public RegisteredClient toRegisteredClient(OAuthClient client) {
	
		return RegisteredClient.withId(client.getId().toString())
				.clientId(client.getClientId())
				.clientSecret(client.getClientSecret())
				.authorizationGrantTypes(grantTypes -> {
					for(String t : client.getGrantTypes().split(",")) {
						grantTypes.add(new AuthorizationGrantType(t.trim()));
					}
				}).scopes(scopes -> {
					for(String scope : client.getScopes().split(",")) {
						scopes.add(scope.trim());
					}
				}).redirectUris(uris -> {
					if(client.getRedirectUris() == null) {
						uris.clear();
					}else {
						for(String uri : client.getRedirectUris().split(",")) {
							uris.add(uri.trim());
						}
					}
				}).tokenSettings(TokenSettings.builder()
						.accessTokenTimeToLive(Duration.ofSeconds(client.getAccessTokenValidationSeconds()))
						.refreshTokenTimeToLive(Duration.ofSeconds(client.getRefreshTokenValidationSeconds()))
						.reuseRefreshTokens(true).build())
				.clientAuthenticationMethods(methods -> methods.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC))
				.clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
				.build();
	}
}
