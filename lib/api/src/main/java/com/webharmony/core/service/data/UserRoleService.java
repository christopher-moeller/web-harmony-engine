package com.webharmony.core.service.data;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.api.rest.model.UserRoleDto;
import com.webharmony.core.data.jpa.model.user.AppUserAccessConfig;
import com.webharmony.core.data.jpa.model.user.AppUserRole;
import com.webharmony.core.data.jpa.model.user.QAppUserAccessConfig;
import com.webharmony.core.service.data.utils.AbstractEntityCrudService;
import com.webharmony.core.utils.exceptions.InternalServerException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserRoleService extends AbstractEntityCrudService<UserRoleDto, AppUserRole> {

    private static final QAppUserAccessConfig qAppUserAccessConfig = QAppUserAccessConfig.appUserAccessConfig;

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void deleteEntity(AppUserRole entity) {

        if(entity.isCreatedByEnum())
            throw new InternalServerException("Role is not deletable");

        deleteRoleReferencesInUserAccessConfig(entity);
        super.deleteEntity(entity);

    }

    private void deleteRoleReferencesInUserAccessConfig(AppUserRole role) {
        final List<AppUserAccessConfig> relevantAccessConfigEntries = new JPAQuery<>(em)
                .select(qAppUserAccessConfig)
                .from(qAppUserAccessConfig)
                .where(qAppUserAccessConfig.mainRole.eq(role).or(qAppUserAccessConfig.roles.contains(role)))
                .fetch();

        for (AppUserAccessConfig configEntry : relevantAccessConfigEntries) {
            if(configEntry.getRoles() != null) {
                configEntry.getRoles().removeIf(e -> e.getUuid().equals(role.getUuid()));
            }
            if(configEntry.getMainRole() != null && configEntry.getMainRole().getUuid().equals(role.getUuid())) {
                configEntry.setMainRole(null);
            }

            em.persist(configEntry);
        }
    }
}
