package com.webharmony.core.data.jpa.repository;

import com.webharmony.core.data.jpa.model.AppServerTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ServerTaskRepository extends JpaRepository<AppServerTask, UUID> {

    Optional<AppServerTask> findByTaskId(String taskId);

}
