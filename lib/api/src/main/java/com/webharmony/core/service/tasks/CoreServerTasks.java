package com.webharmony.core.service.tasks;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.data.jpa.model.user.AppUserRole;
import com.webharmony.core.data.jpa.model.user.QAppUserRole;
import com.webharmony.core.data.jpa.repository.HarmonyEventRepository;
import com.webharmony.core.service.tasks.utils.AbstractServerTaskHandler;
import com.webharmony.core.service.tasks.utils.Task;
import com.webharmony.core.utils.exceptions.TestBackendException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@SuppressWarnings("unused")
@Component
public class CoreServerTasks implements AbstractServerTaskHandler {

    @PersistenceContext
    private EntityManager em;

    private static final QAppUserRole qAppUserRole = QAppUserRole.appUserRole;

    private final HarmonyEventRepository harmonyEventRepository;

    public CoreServerTasks(HarmonyEventRepository harmonyEventRepository) {
        this.harmonyEventRepository = harmonyEventRepository;
    }

    @Task(name = "Clear invalid or outdated access rights from actor entities")
    public void clearInvalidAccessRights() {
        final List<AppUserRole> allRoles = new JPAQuery<>(em).select(qAppUserRole)
                .from(qAppUserRole)
                .leftJoin(qAppUserRole.includedRights).fetchJoin()
                .fetch();

    }

    @Task(name = "Clear harmony events")
    public void clearHarmonyEvents() {
        harmonyEventRepository.deleteAll();
    }

    @Task(name = "Throw an test exception")
    public void throwAnException() {
        throw new TestBackendException();
    }

    @Override
    public String getHandlerName() {
        return "Core Tasks";
    }
}
