package com.webharmony.core.data.jpa.repository;

import com.webharmony.core.data.jpa.model.AppCronJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CronJobRepository extends JpaRepository<AppCronJob, UUID> {

    Optional<AppCronJob> findByJavaClass(String javaClass);

}
