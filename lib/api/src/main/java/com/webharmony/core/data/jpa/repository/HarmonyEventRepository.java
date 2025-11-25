package com.webharmony.core.data.jpa.repository;

import com.webharmony.core.data.jpa.model.harmonyevent.AppHarmonyEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HarmonyEventRepository extends JpaRepository<AppHarmonyEvent, UUID> {

}
