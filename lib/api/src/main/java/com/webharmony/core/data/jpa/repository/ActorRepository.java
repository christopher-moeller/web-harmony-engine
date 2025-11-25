package com.webharmony.core.data.jpa.repository;

import com.webharmony.core.data.jpa.model.actor.AbstractActor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ActorRepository extends JpaRepository<AbstractActor, UUID> {

    Optional<AbstractActor> findByUniqueName(String uniqueName);

}
