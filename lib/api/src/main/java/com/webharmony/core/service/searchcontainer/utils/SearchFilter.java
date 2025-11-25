package com.webharmony.core.service.searchcontainer.utils;

import com.webharmony.core.api.rest.model.utils.optioncontainers.utils.AbstractSelectableOptionsContainer;
import com.webharmony.core.api.rest.model.utils.optioncontainers.utils.AbstractUnPagedOptionsContainer;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.service.SelectableOptionContainerService;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public interface SearchFilter {

    String getFilterName();
    String getFilterDescription();

    @SuppressWarnings("rawtypes")
    FilterType getFilterType();

    interface FilterType<T> {
        Schema<T> getSwaggerSchema();
    }

    class StringFilter implements FilterType<String> {

        @Override
        public Schema<String> getSwaggerSchema() {
            return new StringSchema();
        }
    }

    class BooleanFilter implements FilterType<Boolean> {

        @Override
        public Schema<Boolean> getSwaggerSchema() {
            return new BooleanSchema();
        }
    }

    class SingleSelectionEnumFilter implements FilterType<String> {

        private final Class<? extends Enum<?>> enumClass;
        public SingleSelectionEnumFilter(Class<? extends Enum<?>> enumClass) {
            this.enumClass = enumClass;
        }

        public List<SearchFilterSelectionOption> getOptions() {
            return Stream.of(this.enumClass.getEnumConstants())
                    .map(e -> new SearchFilterSelectionOption(e.name(), e.name()))
                    .toList();
        }

        @Override
        public Schema<String> getSwaggerSchema() {
            return new StringSchema();
        }
    }

    record SingleSelectionFilter(
            Class<? extends AbstractSelectableOptionsContainer<?>> optionsContainerClass) implements FilterType<String> {

        public List<SearchFilterSelectionOption> getOptions() {
                if (optionsContainerClass != null) {
                    AbstractSelectableOptionsContainer<?> optionsContainer = ContextHolder.getContext().getBean(SelectableOptionContainerService.class).getOptionsContainerByClass(optionsContainerClass);
                    if (optionsContainer instanceof AbstractUnPagedOptionsContainer<?> unPagedContainer) {
                        return unPagedContainer.getSelectableOptionsAsFilterOptions();
                    }
                }
                return Collections.emptyList();
            }


            @Override
            public Schema<String> getSwaggerSchema() {
                return new StringSchema();
            }
        }

}
