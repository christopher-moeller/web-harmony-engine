package com.webharmony.core.service.cron.jobs;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.data.jpa.model.token.QAppToken;
import com.webharmony.core.service.TokenService;
import com.webharmony.core.service.cron.AbstractCronJob;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.SneakyThrows;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class TokenCleanCronJob extends AbstractCronJob {

    private static final QAppToken qToken = QAppToken.appToken;

    private final TokenService tokenService;

    @PersistenceContext
    private EntityManager em;

    public TokenCleanCronJob(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public String getLabel() {
        return "Token Clean Cron Job";
    }

    @Override
    public String getDescription() {
        return "This job cleans all expired tokens";
    }

    @Override
    @SneakyThrows
    @Transactional
    public void executeInternal() {
        tokenService.deleteAllById(findAllExpiredTokens());
    }

    @Transactional(readOnly = true)
    public List<UUID> findAllExpiredTokens() {
        return new JPAQuery<>(em)
                .select(qToken.uuid)
                .from(qToken)
                .where(qToken.expiresAt.before(LocalDateTime.now()))
                .fetch();
    }

    @Override
    public Trigger getTrigger() {
        return new CronTrigger("0 0 1 * * *");
    }
}
