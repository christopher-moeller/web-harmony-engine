package com.webharmony.core.testutils;

import com.webharmony.core.data.jpa.model.user.AppUser;
import com.webharmony.core.service.data.UserCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestFixturesInitializer {

    private static boolean isInitialized = false;

    @Autowired
    private UserCrudService userCrudService;

    public synchronized void init() {
        if(!isInitialized) {
            initTestUsers();
            isInitialized = true;
        }
    }

    private void initTestUsers() {
        for (ETestUser eTestUser : ETestUser.values()) {
            AppUser appUser = eTestUser.buildNewAppUserEntity();
            userCrudService.saveEntity(appUser);
        }
    }

}
