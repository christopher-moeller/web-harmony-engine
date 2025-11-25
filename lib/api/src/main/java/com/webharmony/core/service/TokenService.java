package com.webharmony.core.service;

import com.webharmony.core.data.jpa.model.token.AppToken;
import com.webharmony.core.data.jpa.model.token.ETokenType;
import com.webharmony.core.data.jpa.repository.TokenRepository;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.utils.exceptions.BadRequestException;
import com.webharmony.core.utils.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TokenService implements I18nTranslation {

    private final I18N i18N = createI18nInstance(TokenService.class);

    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public AppToken persistNewToken(String tokenValue, ETokenType type, Object payload, LocalDateTime expiresAt) {

        AppToken appToken = new AppToken();
        appToken.setTokenValue(tokenValue);
        appToken.setType(type);
        appToken.setTypedPayload(payload);
        appToken.setExpiresAt(expiresAt);

        return tokenRepository.save(appToken);
    }

    @Transactional(readOnly = true)
    public AppToken findTokenByValue(String tokenValue) {
        return tokenRepository.findByTokenValue(tokenValue)
                .orElseThrow(() -> new NotFoundException(i18N.translate("Token '{tokenValue}' not found").add("tokenValue", tokenValue).build()));
    }

    public boolean isTokenValid(String tokenValue, ETokenType expectedType) {
        return tokenRepository.findByTokenValue(tokenValue)
                .map(token -> isTokenValid(token, expectedType))
                .orElse(false);
    }

    public boolean isTokenValid(AppToken appToken, ETokenType expectedType) {
        if(!appToken.getType().equals(expectedType)) {
            return false;
        }
        return LocalDateTime.now().isBefore(appToken.getExpiresAt());
    }

    @Transactional
    public void useToken(AppToken appToken, ETokenType eTokenType) {

        if(!isTokenValid(appToken, eTokenType))
            throw new BadRequestException(i18N.translate("Token is not valid").build());

        if(appToken.getType().getIsOneTimeToken()) {
            tokenRepository.delete(appToken);
        }

    }

    public String createRandomTokenValue() {
        return UUID.randomUUID().toString();
    }

    @Transactional
    public void deleteAllById(List<UUID> allExpiredTokens) {
        this.tokenRepository.deleteAllById(allExpiredTokens);
    }

    public void deleteAllTokens() {
        tokenRepository.deleteAll();
    }


    @Transactional
    public void deleteByTokenValue(String rawToken) {
        tokenRepository.findByTokenValue(rawToken).ifPresent(tokenRepository::delete);
    }
}
