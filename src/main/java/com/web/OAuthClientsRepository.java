package com.web;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OAuthClientsRepository extends JpaRepository<OAuthClient, Long>{

	Optional<OAuthClient> findByClientId(String clientId);
}
