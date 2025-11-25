package com.webharmony.core.service;

import com.webharmony.core.api.rest.model.SecureKeyDto;
import com.webharmony.core.configuration.EProfile;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.enums.utils.SecureKeyEnum;
import com.webharmony.core.utils.CollectionUtils;
import com.webharmony.core.utils.StringUtils;
import com.webharmony.core.utils.dev.DevUtils;
import com.webharmony.core.utils.dev.LocalDevProperties;
import com.webharmony.core.utils.dev.SecureKeysProperties;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SecureKeyService {

    private final InMemoryDataService inMemoryDataService;

    public SecureKeyService(InMemoryDataService inMemoryDataService) {
        this.inMemoryDataService = inMemoryDataService;
    }

    public SecureKeyDto getSecureKeyByName(String name) {
        return inMemoryDataService.getAllEntries(SecureKeyDto.class)
                .stream()
                .filter(k -> k.getName().equals(name))
                .findAny()
                .orElseThrow();
    }

    private void initEnums() {
        for (Class<? extends SecureKeyEnum> secureKeyEnumClass : ReflectionUtils.getAllProjectClassesImplementingSuperClass(SecureKeyEnum.class)) {
            if(Enum.class.isAssignableFrom(secureKeyEnumClass)) {
                for (SecureKeyEnum enumConstant : secureKeyEnumClass.getEnumConstants()) {
                    SecureKeyDto dto = new SecureKeyDto();
                    dto.setId(UUID.randomUUID().toString());
                    dto.setName(enumConstant.name());
                    inMemoryDataService.addEntry(dto);
                }

            }
        }
    }

    private void initDevConfig() {
        SecureKeysProperties secureKeysProperties = DevUtils.loadLocalDevProperties().map(LocalDevProperties::getSecureKeys).orElse(null);
        if(secureKeysProperties == null || !secureKeysProperties.getImportEntries())
            return;

        for (SecureKeyDto secureKeyDto : CollectionUtils.emptyListIfNull(secureKeysProperties.getEntries())) {
            inMemoryDataService.getAllEntries(SecureKeyDto.class)
                    .stream()
                    .filter(e -> e.getName().equals(secureKeyDto.getName()))
                    .findAny()
                    .ifPresent(e -> e.setKey(secureKeyDto.getKey()));
        }

    }

    public List<SecureKeyDto> getEmptyEntries() {
        return inMemoryDataService.getAllEntries(SecureKeyDto.class)
                .stream()
                .filter(e -> StringUtils.isNullOrEmpty(e.getKey()))
                .toList();
    }

    public void init() {
        initEnums();
        if(ContextHolder.getContext().isProfileActive(EProfile.DEV))
            initDevConfig();
    }
}
