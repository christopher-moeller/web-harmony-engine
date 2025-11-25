package com.webharmony.core.configuration.security;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
public class ApplicationAccessRule {

    private final List<ApplicationRight> applicationRights;

    private final boolean isOrConnected;

    private final boolean isRestricted;

    private ApplicationAccessRule(boolean isOrConnected, ApplicationRight... applicationRights) {
        this.applicationRights = List.of(applicationRights);
        this.isOrConnected = isOrConnected;
        this.isRestricted = true;
    }

    private ApplicationAccessRule() {
        this.isRestricted = false;
        this.isOrConnected = false;
        this.applicationRights = new ArrayList<>();
    }

    public static ApplicationAccessRule of(ApplicationRight[] applicationRights, boolean isOrConnected) {
        return new ApplicationAccessRule(isOrConnected, applicationRights);
    }

    public static ApplicationAccessRule ofUnrestricted() {
        return new ApplicationAccessRule();
    }

    public boolean hasAccess(Set<ApplicationRight> actorsRights) {
        if(!isRestricted)
            return true;

        if(isOrConnected) {
            for (ApplicationRight applicationRight : this.applicationRights) {
                if (actorsRights.contains(applicationRight))
                    return true;
            }

            return false;
        } else {
            for (ApplicationRight applicationRight : this.applicationRights) {
                if(!actorsRights.contains(applicationRight))
                    return false;
            }

            return true;
        }
    }
}
