package com.webharmony.core.data.jpa.repository;

import com.webharmony.core.data.jpa.model.token.AppToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<AppToken, UUID> {

    Optional<AppToken> findByTokenValue(String tokenValue);

}
