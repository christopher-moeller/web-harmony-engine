package com.webharmony.core.testutils;

import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.data.enums.ECoreUserRoles;
import com.webharmony.core.data.enums.utils.PersistenceEnum;
import com.webharmony.core.data.jpa.model.user.AppUser;
import com.webharmony.core.data.jpa.model.user.AppUserAccessConfig;
import com.webharmony.core.service.data.UserCrudService;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.stream.Collectors;

public enum ETestUser {

    ADMIN_USER("admin@webharmony.de", true),
    BASE_USER_1("base_user@webharmony.de", false),
    USER_WITH_DEV_RIGHTS("user_with_dev_rights@webharmony.de", false);

    @Getter
    private final String email;
    private final boolean isAdmin;
    @Getter
    private final Set<ECoreActorRight> actorRights;

    ETestUser(String email, boolean isAdmin, ECoreActorRight... actorRights) {
        this.email = email;
        this.isAdmin = isAdmin;
        this.actorRights = Set.of(actorRights);
    }

    public AppUser buildNewAppUserEntity() {
        AppUser appUser = new AppUser();
        appUser.setEmail(email);
        appUser.setFirstname("Firstname of "+email);
        appUser.setLastname("Lastname of "+email);
        appUser.setPassword(ContextHolder.getContext().getBean(PasswordEncoder.class).encode("*"));
        appUser.setUserAccessConfig(new AppUserAccessConfig());

        appUser.getUserAccessConfig().setIsAdmin(false);
        appUser.getUserAccessConfig().setRoles(Set.of(ECoreUserRoles.STANDARD_USER.getEntity()));
        appUser.getUserAccessConfig().setMainRole(ECoreUserRoles.STANDARD_USER.getEntity());
        appUser.getUserAccessConfig().setIsAdmin(isAdmin);
        appUser.getUserAccessConfig().setAdditionalRights(actorRights.stream().map(PersistenceEnum::getEntity).collect(Collectors.toSet()));

        return appUser;
    }

    public AppUser loadUser() {
        return ContextHolder.getContext().getBean(UserCrudService.class).getUserEntityByEmail(email);
    }
}
