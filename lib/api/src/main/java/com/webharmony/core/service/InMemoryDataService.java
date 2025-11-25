package com.webharmony.core.service;

import com.webharmony.core.utils.assertions.Assert;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InMemoryDataService {

    private final Map<Class<?>, List<?>> dataMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public synchronized <T> List<T> getAllEntries(Class<T> dataType) {
        if(!dataMap.containsKey(dataType))
            return Collections.emptyList();

        return (List<T>) dataMap.get(dataType);
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> void addEntry(T entry) {
        Assert.isNotNull(entry).verify();
        final Class<T> type = (Class<T>) entry.getClass();
        dataMap.computeIfAbsent(type, t -> new ArrayList<>());
        List<T> dataList = (List<T>) dataMap.get(type);
        dataList.add(entry);
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> void removeEntry(T entry) {
        Assert.isNotNull(entry).verify();
        final Class<T> type = (Class<T>) entry.getClass();
        if(dataMap.containsKey(type))
            dataMap.get(type).remove(entry);
    }

}
