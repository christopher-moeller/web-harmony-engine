package com.webharmony.core.service.authentication;

import com.webharmony.core.service.authentication.types.UserBlockedEntry;
import com.webharmony.core.service.authentication.types.UserBlockedException;
import com.webharmony.core.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserLoginBlockService {

    private final Map<String, UserBlockedEntry> usernameBlockedMap = new HashMap<>();

    public void addLoginErrorAttempt(String username) {
        final UserBlockedEntry blockEntry = usernameBlockedMap.computeIfAbsent(username, key -> {
            final UserBlockedEntry entry = new UserBlockedEntry();
            entry.setBlockedFrom(DateUtils.getDateTimeNow());
            return entry;
        });

        blockEntry.addLoginErrorAttempt();
    }

    public void assertUserIsNotBlocked(String username) throws UserBlockedException {
        getUserBlockedEntryByUser(username)
                .ifPresent(entry -> {
                    throw new UserBlockedException(entry);
                });
    }

    public Optional<UserBlockedEntry> getUserBlockedEntryByUser(String username) {
        return Optional.ofNullable(usernameBlockedMap.get(username));
    }

    public void removeBlockedUser(String userName) {
        usernameBlockedMap.remove(userName);
    }
}
