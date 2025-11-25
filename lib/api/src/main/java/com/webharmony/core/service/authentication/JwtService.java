package com.webharmony.core.service.authentication;

import com.webharmony.core.configuration.EProfile;
import com.webharmony.core.configuration.utils.EnvironmentConstants;
import com.webharmony.core.data.jpa.model.actor.AbstractActor;
import com.webharmony.core.utils.DateUtils;
import com.webharmony.core.utils.FileUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtService {

    private static final Path LOCAL_KEY_FILE = Path.of(".local-auth-key");

    @Getter
    @Value( "${" + EnvironmentConstants.ENV_AUTHENTICATION_JWT_EXPIRATION_HOURS + "}" )
    private long expirationHours;

    @Getter
    @Value( "${" + EnvironmentConstants.ENV_AUTHENTICATION_JWT_JWT_TYPE + "}" )
    private String jwtType;

    private SecretKey key;

    public void initKey(ApplicationContext applicationContext) {
        this.key = createNewKey(applicationContext);
    }

    private SecretKey createNewKey(ApplicationContext context) {
        if(List.of(context.getEnvironment().getActiveProfiles()).contains(EProfile.DEV.getSpringName())) {
            final SecretKey localFileKey = loadKeyFromLocalFile().orElseGet(this::generateNewKey);
            writeKeyToLocalFile(localFileKey);
            return localFileKey;
        } else {
            return generateNewKey();
        }
    }

    private Optional<SecretKey> loadKeyFromLocalFile() {
        if(LOCAL_KEY_FILE.toFile().exists()) {
            byte[] bytes = FileUtils.readBytesFromFile(LOCAL_KEY_FILE);
            return Optional.of(Keys.hmacShaKeyFor(bytes));
        } else {
            return Optional.empty();
        }
    }

    private SecretKey generateNewKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public String generateJwtToken(AbstractActor actor, LocalDateTime expiration) {

        final Date issuedAt = DateUtils.toDate(LocalDateTime.now());

        return Jwts.builder()
                .setSubject(actor.getUniqueName())
                .setIssuedAt(issuedAt)
                .setExpiration(DateUtils.toDate(expiration))
                .signWith(key)
                .compact();
    }

    public String getRawToken(String token) {
        return token.replace(getJwtType() + " ", "");
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private void writeKeyToLocalFile(SecretKey devKey) {
        FileUtils.writeToFile(LOCAL_KEY_FILE, devKey.getEncoded());
    }


}
