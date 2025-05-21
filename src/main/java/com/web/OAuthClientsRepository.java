package com.web;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

@Profile("test")
public interface OAuthClientsRepository extends JpaRepository<OAuthClient, Long>{

	Optional<OAuthClient> findByClientId(String clientId);
}
