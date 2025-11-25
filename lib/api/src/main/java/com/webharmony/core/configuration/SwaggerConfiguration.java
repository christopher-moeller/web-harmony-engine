package com.webharmony.core.configuration;

import com.webharmony.core.api.rest.controller.utils.AbstractCrudController;
import com.webharmony.core.api.rest.controller.utils.RestRequestParams;
import com.webharmony.core.configuration.utils.EnvironmentConstants;
import com.webharmony.core.service.searchcontainer.AbstractSearchContainer;
import com.webharmony.core.service.searchcontainer.utils.SearchContainerAttribute;
import com.webharmony.core.service.searchcontainer.utils.SearchFilter;
import com.webharmony.core.service.searchcontainer.utils.SearchFilterSelectionOption;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@Profile(EnvironmentConstants.PROFILE_DEV)
public class SwaggerConfiguration {

    public static final String SWAGGER_UI_PATH = "swagger-ui/index.html";

    private static final String SWAGGER_ATTRIBUTE_DEFAULT = "default '%s'";

    private final ApplicationContext applicationContext;

    public SwaggerConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
               .info(new Info()
                       .title("My API")
                       .version("1.0.0")
                       .description("This is a sample server Petstore server.  You can find out more about Swagger "));
    }

    @Bean
    public OperationCustomizer routerOperationCustomizer() {
        return this::customizeOperation;
    }

    private Operation customizeOperation(Operation operation, HandlerMethod handler) {
        getRestRequestParameterType(handler)
                .ifPresent(param -> customizeOperationForRestRequestParams(operation, handler, param));
        return operation;
    }

    private Optional<MethodParameter> getRestRequestParameterType(HandlerMethod handler) {
        return Stream.of(handler.getMethodParameters()).filter(p -> p.getParameterType().equals(RestRequestParams.class)).findAny();
    }

    private void customizeOperationForRestRequestParams(Operation operation, HandlerMethod handler, MethodParameter methodParameter) {
        String parameterName = methodParameter.getParameter().getName();
        List<Parameter>  operationParameters = operation.getParameters();
        operationParameters.removeIf(p -> p.getName().equals(parameterName));

        if(isUnPagedRequestAllowedForResource(handler)) {
            operationParameters.add(createOptionalParameter(RestRequestParams.IS_PAGED_PARAM, new BooleanSchema(), String.format(SWAGGER_ATTRIBUTE_DEFAULT, RestRequestParams.DEFAULT_IS_PAGED_VALUE)));
        }


        operationParameters.add(createOptionalParameter(RestRequestParams.PAGE_PARAM, new NumberSchema(), String.format(SWAGGER_ATTRIBUTE_DEFAULT, RestRequestParams.DEFAULT_PAGE_NUMBER)));
        operationParameters.add(createOptionalParameter(RestRequestParams.SIZE_PARAM, new NumberSchema(), String.format(SWAGGER_ATTRIBUTE_DEFAULT, RestRequestParams.DEFAULT_PAGE_SIZE)));

        AbstractCrudController<?> crudController = (AbstractCrudController<?>) applicationContext.getBean(handler.getBeanType());
        AbstractSearchContainer searchContainer = crudController.getSearchContainer();

        String availableAttributes = searchContainer.getAvailableAttributes()
                .stream()
                .map(SearchContainerAttribute::getName)
                .collect(Collectors.joining(", "));

        operationParameters.add(createOptionalParameter(RestRequestParams.ATTRIBUTES_PARAM, new ArraySchema(), availableAttributes));
        operationParameters.add(createOptionalParameter(RestRequestParams.SORT_PARAM, new ArraySchema(), String.join(", ", searchContainer.getAvailableSortAttributes())));

        operationParameters.addAll(getFilterParameterBySearchContainer(searchContainer));

    }

    private List<Parameter> getFilterParameterBySearchContainer(AbstractSearchContainer searchContainer) {
        return searchContainer.getAvailableSearchFilter()
                .stream()
                .map(searchFilter -> createOptionalParameter(searchFilter.getFilterName(), searchFilter.getFilterType(), searchFilter.getFilterDescription()))
                .toList();
    }

    private Parameter createOptionalParameter(String name, Schema<?> schema, String description) {
        Parameter parameter = new Parameter();
        parameter.setName(name);
        parameter.setIn("query");
        parameter.setStyle(Parameter.StyleEnum.SIMPLE);
        parameter.setSchema(schema);
        parameter.setRequired(false);
        parameter.setDescription(description);
        return parameter;
    }

    private Parameter createOptionalParameter(String name, SearchFilter.FilterType<?> filterType, String description) {
        Parameter parameter = new Parameter();
        parameter.setName(name);
        parameter.setIn("query");
        parameter.setStyle(Parameter.StyleEnum.SIMPLE);
        parameter.setSchema(filterType.getSwaggerSchema());
        parameter.setRequired(false);
        parameter.setDescription(description);

        if(filterType instanceof SearchFilter.SingleSelectionFilter selectionFilter) {
            for(SearchFilterSelectionOption option : selectionFilter.getOptions()) {
                Example example = new Example();
                example.setValue(option.getValue());
                example.setDescription(option.getLabel());

                parameter.addExample(option.getLabel(), example);
            }
        }

        if(filterType instanceof SearchFilter.SingleSelectionEnumFilter selectionEnumFilter) {
            for(SearchFilterSelectionOption option : selectionEnumFilter.getOptions()) {
                Example example = new Example();
                example.setValue(option.getValue());
                example.setDescription(option.getLabel());

                parameter.addExample(option.getLabel(), example);
            }
        }

        return parameter;
    }

    private boolean isUnPagedRequestAllowedForResource(HandlerMethod handler) {
        if(AbstractCrudController.class.isAssignableFrom(handler.getBeanType())) {
            AbstractCrudController<?> crudController = (AbstractCrudController<?>) applicationContext.getBean(handler.getBeanType());
            return crudController.getSearchContainer().isUnPagedRequestAllowed();
        } else {
            return true;
        }
    }
}
