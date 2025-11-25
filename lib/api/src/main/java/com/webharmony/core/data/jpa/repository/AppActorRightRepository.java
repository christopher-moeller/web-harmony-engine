package com.webharmony.core.data.jpa.repository;

import com.webharmony.core.data.jpa.model.user.AppActorRight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AppActorRightRepository extends JpaRepository<AppActorRight, UUID> {

    List<AppActorRight> findByIsAllowedForUnknownPublicActor(Boolean isAllowedForUnknownPublicActor);
    List<AppActorRight> findByIsAllowedForSystemActor(Boolean isAllowedForSystemActor);
}