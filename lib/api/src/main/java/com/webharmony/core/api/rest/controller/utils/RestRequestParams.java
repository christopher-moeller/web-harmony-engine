package com.webharmony.core.api.rest.controller.utils;

import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.searchcontainer.utils.SortOrder;
import com.webharmony.core.utils.exceptions.InternalServerException;
import lombok.Getter;

import java.util.*;

public class RestRequestParams implements I18nTranslation {

    private final I18N i18N = createI18nInstance(RestRequestParams.class);

    public static final String PAGE_PARAM = "page";
    public static final String SIZE_PARAM = "size";
    public static final String ATTRIBUTES_PARAM = "attributes";
    public static final String SORT_PARAM = "sort";

    public static final String IS_PAGED_PARAM = "isPaged";

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_NUMBER = 0;

    public static final boolean DEFAULT_IS_PAGED_VALUE = true;

    @Getter
    private final Map<String, Object> nativeParameters;

    @Getter
    private final int page;

    @Getter
    private final boolean isPaged;

    @Getter
    private final int size;

    @Getter
    private final List<String> attributes;

    @Getter
    private final List<RestSort> sorts;

    private RestRequestParams(Map<String, Object> nativeParameters) {
        this.nativeParameters = nativeParameters;
        this.isPaged = getParamAsBoolean(IS_PAGED_PARAM).orElse(DEFAULT_IS_PAGED_VALUE);
        this.page = getParamAsInteger(PAGE_PARAM).orElse(DEFAULT_PAGE_NUMBER);
        this.size = getParamAsInteger(SIZE_PARAM).orElse(DEFAULT_PAGE_SIZE);
        this.attributes = getParamAsStringArray(ATTRIBUTES_PARAM).orElseGet(Collections::emptyList);
        this.sorts = createSortList();
    }

    public static RestRequestParams of(Map<String, Object> nativeParameters) {
        return new RestRequestParams(nativeParameters);
    }

    public Optional<Object> getParamAsObject(String key) {
        return Optional.ofNullable(nativeParameters.get(key));
    }

    public Optional<Integer> getParamAsInteger(String key) {
        Object param = nativeParameters.get(key);
        if(param == null) {
            return Optional.empty();
        } else if (param instanceof Integer intValue) {
            return Optional.of(intValue);
        } else {
            try {
                return Optional.of(Integer.parseInt(param.toString()));
            } catch (ClassCastException e) {
                throw new InternalServerException(i18N.translate("Invalid parameter '{parameter}'. Type '{type}' cannot be converted to number.").add("parameter", key).add("type", param.getClass().getSimpleName()).build());
            }
        }
    }
    public Optional<List<String>> getParamAsStringArray(String key) {
        return getParamAsString(key)
                .map(v -> v.split(","))
                .map(List::of)
                .map(values -> values.stream().map(String::trim).toList());

    }

    public Optional<Boolean> getParamAsBoolean(String key) {
        return getParamAsString(key)
                .map(Boolean::parseBoolean);
    }

    public Optional<String> getParamAsString(String key) {
        return Optional.ofNullable(nativeParameters.get(key))
                .map(String::valueOf);
    }

    private List<RestSort> createSortList() {
        return getParamAsStringArray(SORT_PARAM)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(v -> !v.isEmpty())
                .map(RestSort::of)
                .toList();
    }

    @Getter
    public static final class RestSort {
        private final String name;
        private final SortOrder order;

        private RestSort(String name, SortOrder order) {
            this.name = name;
            this.order = order;
        }

        public RestSort createCopyWithNewName(String newName) {
            return new RestSort(newName, this.order);
        }


        public static RestSort of(String param) {
            String[] fragments = param.split(":");
            if(fragments.length > 1) {
                return new RestSort(fragments[0], SortOrder.getOrDefault(fragments[1]));
            } else {
                return new RestSort(fragments[0], SortOrder.getDefault());
            }
        }

    }
}

