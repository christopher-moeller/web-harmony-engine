package com.webharmony.core.service.cron.jobs;

import com.webharmony.core.service.cron.AbstractCronJob;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SleepAWhileJob extends AbstractCronJob {


    @Override
    public String getLabel() {
        return "Sleep a while job";
    }

    @Override
    public String getDescription() {
        return "Test cron job that sleeps for 30 seconds";
    }

    @Override
    @SneakyThrows
    protected void executeInternal() {
        int remainingSeconds = 30;
        while (remainingSeconds > 0) {
            log.info("Remaining sleeping seconds: "+remainingSeconds);
            Thread.sleep(1000);
            remainingSeconds--;
        }
    }

    @Override
    public Trigger getTrigger() {
        return new CronTrigger("0 0 1 * * *");
    }

    @Override
    public boolean isActivatedByDefault() {
        return false;
    }
}
