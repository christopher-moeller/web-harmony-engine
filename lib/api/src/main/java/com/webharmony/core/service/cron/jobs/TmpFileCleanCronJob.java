package com.webharmony.core.service.cron.jobs;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.data.jpa.model.files.QAppFile;
import com.webharmony.core.service.cron.AbstractCronJob;
import com.webharmony.core.service.data.FileService;
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
public class TmpFileCleanCronJob extends AbstractCronJob {

    private static final QAppFile qFile = QAppFile.appFile;

    @PersistenceContext
    private EntityManager em;

    private final FileService fileService;

    public TmpFileCleanCronJob(FileService fileService) {
        this.fileService = fileService;
    }



    @Override
    public String getLabel() {
        return "Temp File Clean Cron Job";
    }

    @Override
    public String getDescription() {
        return "This job cleans all expired temp files";
    }

    @Override
    @SneakyThrows
    @Transactional
    public void executeInternal() {
        fileService.deleteAllById(findAllExpiredTmpFiles());
    }

    @Transactional(readOnly = true)
    public List<UUID> findAllExpiredTmpFiles() {
        final LocalDateTime oneDayInPast = LocalDateTime.now().minusDays(1);
        return new JPAQuery<>(em)
                .select(qFile.uuid)
                .from(qFile)
                .where(qFile.createdAt.before(oneDayInPast))
                .fetch();
    }

    @Override
    public Trigger getTrigger() {
        return new CronTrigger("0 0 1 * * *");  // */10 * * * * *
    }
}
