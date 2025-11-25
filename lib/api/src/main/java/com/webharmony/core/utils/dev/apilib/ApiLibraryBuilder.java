package com.webharmony.core.utils.dev.apilib;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.webharmony.core.api.rest.controller.utils.RestRequestParams;
import com.webharmony.core.api.rest.error.ApiErrorWithJavaExceptionInstance;
import com.webharmony.core.api.rest.error.validation.ValidationResultDto;
import com.webharmony.core.api.rest.model.DevApiLibraryBuildDto;
import com.webharmony.core.api.rest.model.utils.ResponseResource;
import com.webharmony.core.api.rest.model.utils.validinput.ValidInputSpecification;
import com.webharmony.core.configuration.security.ApplicationRight;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.service.appresources.utils.CrudResourceInfoWithOverview;
import com.webharmony.core.service.searchcontainer.utils.SortOrder;
import com.webharmony.core.service.webcontent.model.AbstractWebContent;
import com.webharmony.core.service.webcontent.model.BoxAlignment;
import com.webharmony.core.utils.StringUtils;
import com.webharmony.core.utils.dev.DevUtils;
import com.webharmony.core.utils.dev.LocalDevProperties;
import com.webharmony.core.utils.dev.apilib.api.ApiEndpointInfo;
import com.webharmony.core.utils.dev.fepages.PageProviderService;
import com.webharmony.core.utils.dev.fepages.json.PageJson;
import com.webharmony.core.utils.dev.fepages.json.RouterPagesRoot;
import com.webharmony.core.utils.dev.projectconfig.FrontendProjectConfiguration;
import com.webharmony.core.utils.dev.projectconfig.ProjectConfigurationBuilderService;
import com.webharmony.core.utils.exceptions.InternalServerException;
import com.webharmony.core.utils.objects.LabelValueObject;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import com.webharmony.core.utils.tuple.Tuple2;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApiLibraryBuilder {
    private final List<ApiEndpointInfo> apiEndpoints;

    public ApiLibraryBuilder(List<ApiEndpointInfo> allApiEndpoints) {
        this.apiEndpoints = allApiEndpoints;
    }

    public ApiLibraryBuilder() {
        this(Collections.emptyList());
    }

    public DevApiLibraryBuildDto createLib() {
        final Set<Class<?>> neededEnums = createSetOfNeededEnums();

        final Set<Class<?>> javaSchemas = createSetOfNeededSchemas();

        final StringBuilder coreFileBuilder = new StringBuilder();
        final StringBuilder projectFileBuilder = new StringBuilder();


        createImports(coreFileBuilder, projectFileBuilder);

        createTsModelDeclarations(coreFileBuilder, projectFileBuilder, javaSchemas, neededEnums);

        addLineBreaksForNextSection(coreFileBuilder);
        addLineBreaksForNextSection(projectFileBuilder);

        createTsApiResolverSection(coreFileBuilder, projectFileBuilder);

        addLineBreaksForNextSection(coreFileBuilder);
        addLineBreaksForNextSection(projectFileBuilder);

        createTsApiMethodsSection(coreFileBuilder, projectFileBuilder);

        DevApiLibraryBuildDto result = new DevApiLibraryBuildDto();
        result.setCoreSourceCode(coreFileBuilder.toString());
        result.setProjectSourceCode(projectFileBuilder.toString());

        String projectConfigurationJson = ContextHolder.getContext().getBean(ProjectConfigurationBuilderService.class).buildProjectConfigurationJson();
        result.setProjectConfigurationJson(projectConfigurationJson);

        return result;
    }

    public void saveCoreLibFile(String content) {
        saveLibFile(getPathToLocalFeCoreLibFile(), content);
    }

    public void saveProjectLibFile(String content) {
        saveLibFile(getPathToLocalFeProjectLibFile(), content);
    }

    public void saveProjectConfigurationJSONFile(String content) {
        saveLibFile(getPathToLocalFeProjectConfigurationFile(), content);
    }

    private void createImports(StringBuilder coreFileBuilder, StringBuilder projectFileBuilder) {
        createImportSection(coreFileBuilder);
        projectFileBuilder.append("import {AbstractResourceDto, ApiResource, FileWebData, ResponseResource, RestSort, SearchResult, Point2D} from \"@core/CoreApi\";").append("\n");
        createImportSection(projectFileBuilder);
    }

    private void createImportSection(StringBuilder fileBuilder) {
        fileBuilder.append("import { AxiosResponse } from \"axios\"").append("\n").append("\n");
    }

    private void addLineBreaksForNextSection(StringBuilder builder) {
        builder.append("\n").append("\n");
    }

    private void createTsApiResolverSection(StringBuilder coreFileBuilder, StringBuilder projectFileBuilder) {
        createTsApiResolverSection(coreFileBuilder);
        createTsApiResolverSection(projectFileBuilder);
    }

    @SuppressWarnings("all")
    private void createTsApiResolverSection(StringBuilder stringBuilder) {
        stringBuilder.append("\n");
        stringBuilder.append("export interface ApiResolver {\n");
        stringBuilder.append("\tresolveRequest(apiMethod: string, apiPath: string, body?: any): Promise<any>,\n");
        stringBuilder.append("\tresolveUrlForRestRequestParams(baseUrl: string, restRequestParams: RestRequestParams):string\n");
        stringBuilder.append("}\n");

    }

    @SneakyThrows
    private void saveLibFile(Path libFilePath, String content) {
        final File file = libFilePath.toFile();
        if(file.exists())
            Files.delete(libFilePath);

        Files.writeString(libFilePath, content);
    }

    private Set<Class<?>> createSetOfNeededSchemas() {
        final List<Class<?>> schemas = new ArrayList<>();
        schemas.add(ResponseResource.class);
        schemas.add(RestRequestParams.RestSort.class);
        schemas.addAll(ReflectionUtils.buildSetNestedOfAllNestedTypes(FrontendProjectConfiguration.class));
        schemas.add(FrontendProjectConfiguration.class);
        schemas.addAll(ReflectionUtils.buildSetNestedOfAllNestedTypes(ValidationResultDto.class));
        schemas.add(ValidationResultDto.class);
        schemas.addAll(ReflectionUtils.getAllProjectClassesImplementingSuperClass(ValidInputSpecification.class));
        schemas.add(LabelValueObject.class);
        schemas.addAll(ReflectionUtils.buildSetNestedOfAllNestedTypes(CrudResourceInfoWithOverview.class));
        schemas.add(CrudResourceInfoWithOverview.class);
        schemas.addAll(ReflectionUtils.buildSetNestedOfAllNestedTypes(ApiErrorWithJavaExceptionInstance.class));
        schemas.add(ApiErrorWithJavaExceptionInstance.class);

        schemas.add(AbstractWebContent.class);
        schemas.addAll(ReflectionUtils.getAllProjectClassesImplementingSuperClass(AbstractWebContent.class));

        for (ApiEndpointInfo apiEndpoint : this.apiEndpoints) {
            schemas.addAll(apiEndpoint.buildListOfUsedSchemas());
        }
        return new HashSet<>(schemas);
    }

    private Set<Class<?>> createSetOfNeededEnums() {
        final List<Class<?>> enums = new ArrayList<>();
        enums.add(SortOrder.class);
        for (ApiEndpointInfo apiEndpoint : this.apiEndpoints) {
            enums.addAll(apiEndpoint.buildListOfUsedEnums());
        }
        enums.addAll(ReflectionUtils.getAllProjectClassesImplementingSuperClass(ApplicationRight.class));
        enums.add(BoxAlignment.class);
        return new HashSet<>(enums);
    }

    private void createTsModelDeclarations(StringBuilder coreFileBuilder, StringBuilder projectFileBuilder, Set<Class<?>> javaSchemas, Set<Class<?>> neededEnums) {

        final String startingText = "// ################ Model declaration start ################\n";

        coreFileBuilder.append(startingText);
        projectFileBuilder.append(startingText);

        final RouterPagesRoot routerPagesRoot = ContextHolder.getContext().getBean(PageProviderService.class).buildRouterPages();
        coreFileBuilder.append("\n").append(createTsRoutePagesEnumForCore(routerPagesRoot)).append("\n");
        projectFileBuilder.append("\n").append(createTsRoutePagesEnumForProject(routerPagesRoot)).append("\n");

        coreFileBuilder.append("\n").append(createBackendPathEnum(true));
        projectFileBuilder.append("\n").append(createBackendPathEnum(false));

        for (Class<?> neededEnum : neededEnums) {
            final String content = createTsEnumFromJavaClass(neededEnum);
            if(isCoreClass(neededEnum)) {
                coreFileBuilder.append("\n").append(content).append("\n");
            } else {
                projectFileBuilder.append("\n").append(content).append("\n");
            }
        }

        for (Class<?> javaClass : javaSchemas) {
            final String content = createTsInterfaceFromJavaClass(javaClass);
            if(isCoreClass(javaClass)) {
                coreFileBuilder.append("\n").append(content).append("\n");
            } else {
                projectFileBuilder.append("\n").append(content).append("\n");
            }
        }

        coreFileBuilder.append("\n").append(createTsImplementationOfRestRequestParams()).append("\n");
        projectFileBuilder.append("\n").append(createTsImplementationOfRestRequestParams()).append("\n");

        final String endingText = "\n// ################ Model declaration end ################";
        coreFileBuilder.append(endingText);
        projectFileBuilder.append(endingText);
    }

    @SuppressWarnings("all")
    private String createTsImplementationOfRestRequestParams() {
        return "export class RestRequestParams {\n" +
                "\tqueryParameters?: Map<String, any>\n" +
                "\n" +
                "\tconstructor() {\n" +
                "\t\tthis.queryParameters = new Map<String, any>();\n" +
                "\t}\n" +
                "\n" +
                "\tstatic create():RestRequestParams {\n" +
                "\t\treturn new RestRequestParams()\n" +
                "\t}\n" +
                "\n" +
                "\twithPage(page: number): RestRequestParams {\n" +
                "\t\tthis.addQueryParameter(\"page\", page)\n" +
                "\t\treturn this\n" +
                "\t}\n" +
                "\n" +
                "\twithIsPaged(isPaged: boolean): RestRequestParams {\n" +
                "\t\tthis.addQueryParameter(\"isPaged\", isPaged)\n" +
                "\t\treturn this;\n" +
                "\t}\n" +
                "\n" +
                "\twithSize(size: number): RestRequestParams {\n" +
                "\t\tthis.addQueryParameter(\"size\", size)\n" +
                "\t\treturn this;\n" +
                "\t}\n" +
                "\n" +
                "\twithAttributes(attributes: String[]): RestRequestParams {\n" +
                "\t\tthis.addQueryParameter(\"attributes\", attributes.join(\",\"))\n" +
                "\t\treturn this;\n" +
                "\t}\n" +
                "\n" +
                "\twithSorts(sorts: RestSort[]): RestRequestParams {\n" +
                "\t\tconst sortFragments:string[] = []\n" +
                "\t\tfor(let sort of sorts) {\n" +
                "\t\t\tsortFragments.push(sort.name + \":\" + sort.order)\n" +
                "\t\t}\n" +
                "\t\tif(sortFragments.length > 0) {\n" +
                "\t\t\tthis.addQueryParameter(\"sort\", sortFragments.join(\",\"))\n" +
                "\t\t}\n" +
                "\t\treturn this;\n" +
                "\t}\n" +
                "\n" +
                "\taddQueryParameter(key: string, value: any): RestRequestParams {\n" +
                "\t\tthis.queryParameters!.set(key, value);\n" +
                "\t\treturn this;\n" +
                "\t}\n" +
                "\n" +
                "\ttoQueryString(): string {\n" +
                "\t\tconst queryFragments:string[] = []\n" +
                "\t\tfor (let entry of Array.from(this.queryParameters!.entries())) {\n" +
                "\t\t\tlet key = entry[0];\n" +
                "\t\t\tlet value = entry[1];\n" +
                "\t\t\tqueryFragments.push(key + \"=\" + value)\n" +
                "\t\t}\n" +
                "\n" +
                "\t\treturn queryFragments.length > 0 ? \"?\" + queryFragments.join(\"&\") : \"\"\n" +
                "\t}\n" +
                "}";
    }

    private void createGenericPrefixTsApiMethodSection(StringBuilder builder) {
        builder.append("// ################ API Method declaration start ################\n\n");
        builder.append("export default function(apiResolver: ApiResolver) {").append("\n");
        builder.append("\treturn {\n");
        builder.append("\t\tapi() {\n");
        builder.append("\t\t\treturn {");
    }

    private void createGenericSuffixTsApiMethodSection(StringBuilder builder) {
        builder.append("\n");
        builder.append("\t\t\t}");
        builder.append("\n");
        builder.append("\t\t}");
        builder.append("\n");
        builder.append("\t}");
        builder.append("\n");
        builder.append("}");
        builder.append("\n\n// ################ API Method declaration end ################\"");
    }

    private void createTsApiMethodsSection(StringBuilder coreFileBuilder, StringBuilder projectFileBuilder) {

        createGenericPrefixTsApiMethodSection(coreFileBuilder);
        createGenericPrefixTsApiMethodSection(projectFileBuilder);

        final Map<Class<?>, List<ApiEndpointInfo>> apiEndpointsGroupedByController = getApiEndpointsGroupedByController();
        for (Class<?> controllerClass : apiEndpointsGroupedByController.keySet().stream().sorted(Comparator.comparing(Class::getSimpleName)).toList()) {
            final String tsImplementationOfApiController = createTsImplementationOfApiController(controllerClass, apiEndpointsGroupedByController.get(controllerClass));

            final StringBuilder builder = isCoreClass(controllerClass) ? coreFileBuilder : projectFileBuilder;
            builder.append("\n");
            builder.append(tsImplementationOfApiController);
            builder.append("\n");
        }

        createGenericSuffixTsApiMethodSection(coreFileBuilder);
        createGenericSuffixTsApiMethodSection(projectFileBuilder);

    }

    private String createTsImplementationOfApiController(Class<?> controllerClass, List<ApiEndpointInfo> apiEndpointInfos) {

        final StringBuilder builder = new StringBuilder();
        final String methodName = "get" + controllerClass.getSimpleName().replace("Controller", "") + "Api";
        builder.append(String.format("\t\t\t\t%s() {", methodName)).append("\n");
        builder.append("\t\t\t\t\treturn {").append("\n\n");

        for (ApiEndpointInfo apiEndpoint : apiEndpointInfos) {

            Type responseType = apiEndpoint.getResponseType();
            if(responseType.getTypeName().contains("org.springframework.core.io.Resource"))
                continue;

            final String methodTsImplementation = createTsImplementationOfApiMethod(apiEndpoint);
            builder.append(methodTsImplementation).append("\n");
        }

        builder.append("\t\t\t\t\t}").append("\n");
        builder.append("\t\t\t\t},");

        return builder.toString();

    }

    private String createTsImplementationOfApiMethod(ApiEndpointInfo apiEndpoint) {

        final StringBuilder builder = new StringBuilder();

        final String methodName = apiEndpoint.getMethodName();
        final String responseType = buildResponseTypeForTs(apiEndpoint.getResponseType(), apiEndpoint.getControllerClass());

        final Map<String, Tuple2<Type, MethodParameter>> tsMethodParams = getRelevantParamsForTsMethod(apiEndpoint.getHandlerMethod());

        final String parameterSectionForTsFunction = tsMethodParams.entrySet().stream()
                .map(entry -> String.format("%s: %s", entry.getKey(), ApiLibraryBuilder.getTypeScriptType(entry.getValue().getType1())))
                .collect(Collectors.joining(", "));

        builder.append("\t\t\t\t\t\t").append(String.format("async %s(%s):Promise<AxiosResponse<%s, any>> {", methodName, parameterSectionForTsFunction, responseType)).append("\n");

        final String apiMethod = String.format("\"%s\"", apiEndpoint.getRestMethod());
        final String apiUrl = resolveApiUrl(apiEndpoint.getUrl(), tsMethodParams, apiEndpoint.getHandlerMethod());
        final String body = tsMethodParams.containsKey("body") ? "body" : "undefined";

        builder.append("\t\t\t\t\t\t\t").append(String.format("const response = await apiResolver.resolveRequest(%s, %s, %s)", apiMethod, apiUrl, body)).append("\n");
        builder.append("\t\t\t\t\t\t\t").append(String.format("return <AxiosResponse<%s, any>> response", responseType)).append("\n");

        builder.append("\t\t\t\t\t\t").append("},");

        return builder.toString();
    }

    private String resolveApiUrl(String rawUrl, final Map<String, Tuple2<Type, MethodParameter>> tsMethodParams, HandlerMethod handlerMethod) {
        final String baseUrl = resolveBaseApiUrl(rawUrl, tsMethodParams);
        if(tsMethodParams.values().stream().anyMatch(v -> v.getType1().equals(RestRequestParams.class))) {
            final String restRequestParamsParameterName = tsMethodParams.entrySet().stream()
                    .filter(e -> e.getValue().getType1().equals(RestRequestParams.class)).findAny().orElseThrow().getKey();

            return buildApiUrlForRestRequestParams(baseUrl, restRequestParamsParameterName);
        } else if(Arrays.stream(handlerMethod.getMethodParameters()).anyMatch(p -> p.hasParameterAnnotation(RequestParam.class))) {
            return buildApiUrlWithMethodQueryParameters(baseUrl, tsMethodParams);
        } else {
            return baseUrl;
        }
    }

    private String buildApiUrlForRestRequestParams(String baseUrl, String restRequestParamsParameterName) {
        return String.format("apiResolver.resolveUrlForRestRequestParams(%s, %s)", baseUrl, restRequestParamsParameterName);
    }

    private String buildApiUrlWithMethodQueryParameters(String baseUrl, Map<String, Tuple2<Type, MethodParameter>> tsMethodParams) {
        final StringBuilder paramBuilder = new StringBuilder("RestRequestParams.create()");
        for (Map.Entry<String, Tuple2<Type, MethodParameter>> entry : tsMethodParams.entrySet()) {
            final MethodParameter methodParameter = entry.getValue().getType2();
            if(!methodParameter.hasParameterAnnotation(RequestParam.class))
                continue;

            final String paramName = entry.getKey();
            paramBuilder.append(String.format(".addQueryParameter(\"%s\", %s)", paramName, paramName));
        }
        return buildApiUrlForRestRequestParams(baseUrl, paramBuilder.toString());
    }

    private String resolveBaseApiUrl(String rawUrl, final Map<String, Tuple2<Type, MethodParameter>> tsMethodParams) {
        final Set<String> keys = tsMethodParams.keySet();
        boolean containsAtLeastOneKey = keys.stream().anyMatch(key -> rawUrl.contains("{" + key + "}"));
        if(containsAtLeastOneKey) {
            String resolvedUrl = String.format("`%s`", rawUrl);
            for (String key : keys) {
                resolvedUrl = resolvedUrl.replace("{" + key + "}", String.format("${%s}", key));
            }
            return resolvedUrl;
        } else {
            return String.format("\"%s\"", rawUrl);
        }
    }

    private Map<String, Tuple2<Type, MethodParameter>> getRelevantParamsForTsMethod(HandlerMethod controllerMethod) {

        final List<Class<?>> typesToIgnore = Arrays.asList(HttpServletResponse.class, MultipartFile.class);

        final Map<String, Tuple2<Type, MethodParameter>> resultMap = new LinkedHashMap<>();
        for(int i = 0; i < controllerMethod.getMethodParameters().length; i++) {
            final MethodParameter methodParameter = controllerMethod.getMethodParameters()[i];
            if(typesToIgnore.stream().anyMatch(e -> methodParameter.getParameterType().toString().contains(e.getName()))) {
                continue;
            }

            Type genericParameterType = methodParameter.getGenericParameterType();
            if(genericParameterType instanceof TypeVariable<?>) {
                genericParameterType = ReflectionUtils.getGenericTypeByClassAndIndex(controllerMethod.getBeanType(), 0)
                        .orElseThrow();
            }

            final String paramName;
            if(Optional.ofNullable(methodParameter.getParameterAnnotation(PathVariable.class)).map(PathVariable::value).map(StringUtils::isNotNullAndNotEmpty).orElse(false)) {
                paramName = Objects.requireNonNull(methodParameter.getParameterAnnotation(PathVariable.class)).value();
            } else if(Optional.ofNullable(methodParameter.getParameterAnnotation(RequestParam.class)).map(RequestParam::value).map(StringUtils::isNotNullAndNotEmpty).orElse(false)) {
                paramName = Objects.requireNonNull(methodParameter.getParameterAnnotation(RequestParam.class)).value();
            } else if(methodParameter.getParameterAnnotation(RequestBody.class) != null) {
                paramName =  "body";
            }else if(Arrays.stream(controllerMethod.getMethodParameters()).filter(p -> p.getGenericParameterType().equals(methodParameter.getGenericParameterType())).toList().size() == 1){
                paramName = StringUtils.firstLetterToLowerCase(getTypeScriptType(genericParameterType));
            } else {
                paramName = "param"+i;
            }

            resultMap.put(paramName, Tuple2.of(genericParameterType, methodParameter));
        }

        return resultMap;
    }

    private String buildResponseTypeForTs(Type javaType, Class<?> controllerClass) {
        if(javaType instanceof ParameterizedType parameterizedType && parameterizedType.getRawType().equals(ResponseResource.class)) {
            Type resourceType = ((ParameterizedType) controllerClass.getGenericSuperclass()).getActualTypeArguments()[0];
            return ResponseResource.class.getSimpleName() + "<" + getTypeScriptType(resourceType) + ">";
        } else {
            return getTypeScriptType(javaType);
        }
    }

    private Map<Class<?>, List<ApiEndpointInfo>> getApiEndpointsGroupedByController() {
        final Map<Class<?>, List<ApiEndpointInfo>> resultMap = new HashMap<>();
        for (ApiEndpointInfo apiEndpoint : this.apiEndpoints) {
            final Class<?> controllerClass = apiEndpoint.getControllerClass();
            resultMap.computeIfAbsent(controllerClass, key -> new ArrayList<>())
                    .add(apiEndpoint);
        }
        return resultMap;
    }

    @SneakyThrows
    @SuppressWarnings("java:S1192")
    private String createTsEnumFromJavaClass(Class<?> enumClass) {

        final StringBuilder builder = new StringBuilder();
        builder.append(String.format("export enum %s {", getTsClassNameByJavaClass(enumClass) )).append("\n");

        final Object[] enumConstants = enumClass.getEnumConstants();
        for(int i=0; i<enumConstants.length; i++) {
            final Enum<?> enumConstant = (Enum<?>) enumConstants[i];
            final String enumEntryName = enumConstant.name();
            builder.append("\t").append(String.format("%s = \"%s\"", enumEntryName, enumEntryName));
            if(i < enumConstants.length - 1) {
                builder.append(",");
            }

            builder.append("\n");
        }

        builder.append("}");

        return builder.toString();
    }


    private String createTsRoutePagesEnumForCore(RouterPagesRoot routerPagesRoot) {
        final List<PageJson> pages = routerPagesRoot.getPages().stream().filter(PageJson::getIsCorePage).toList();
        return createTsRoutePagesByName("ECoreRouterPage", pages);
    }

    private String createBackendPathEnum(boolean isCore) {
        final String enumName = isCore ? "ECoreBackendPath" : "EProjectBackendPath";
        final List<ApiEndpointInfo> apiEndpointInfos = isCore ? this.apiEndpoints.stream().filter(c -> isCoreClass(c.getControllerClass())).toList() : this.apiEndpoints.stream().filter(c -> !isCoreClass(c.getControllerClass())).toList();

        final List<String> usedPaths = new ArrayList<>();
        final StringBuilder builder = new StringBuilder();
        builder.append(String.format("export enum %s {", enumName )).append("\n");
        for (int i = 0; i < apiEndpointInfos.size(); i++) {
            final ApiEndpointInfo apiEndpointInfo = apiEndpointInfos.get(i);
            final String url = apiEndpointInfo.getUrl()
                    .replace(":.+", "");
            if(usedPaths.contains(url)) {
                continue;
            }

            final String name = createEnumNameOfBackendPathByUrl(url);
            builder.append("\t").append(String.format("%s = \"%s\"", name, url));
            usedPaths.add(url);
            if(i < apiEndpointInfos.size() - 1) {
                builder.append(",");
            }

            builder.append("\n");
        }
        builder.append("}");
        return builder.toString();
    }

    private String createEnumNameOfBackendPathByUrl(String url) {
       final String name = url.replace("/", "_")
               .replace("-", "_")
               .replace("{", "")
               .replace("}", "")
               .replace(":.", "")
               .replace("+", "")
               .toUpperCase();
       return name.startsWith("_") ? name.replaceFirst("_", "") : name;
    }

    private String createTsRoutePagesEnumForProject(RouterPagesRoot routerPagesRoot) {
        final List<PageJson> pages = routerPagesRoot.getPages().stream().filter(p -> !p.getIsCorePage()).toList();
        return createTsRoutePagesByName("EProjectRouterPage", pages);
    }

    private String createTsRoutePagesByName(String enumName, List<PageJson> pages) {
        final StringBuilder builder = new StringBuilder();
        builder.append(String.format("export enum %s {", enumName )).append("\n");
        for(int i=0; i<pages.size(); i++) {
            final PageJson page = pages.get(i);
            final String value = page.getId();
            final String name = value.replace("-", "_")
                    .toUpperCase();

            builder.append("\t").append(String.format("%s = \"%s\"", name, value));
            if(i < pages.size() - 1) {
                builder.append(",");
            }

            builder.append("\n");
        }
        builder.append("}");
        return builder.toString();
    }

    @SneakyThrows
    @SuppressWarnings("all")
    private String createTsInterfaceFromJavaClass(Class<?> javaClass) {

        final List<Class<?>> neededImports = new ArrayList<>();

        final Class<?> superclass = javaClass.getSuperclass();
        boolean hasSuperclass = Optional.ofNullable(superclass)
                .map(Class::getPackage)
                .map(Package::getName)
                .map(packageName -> packageName.startsWith("com.webharmony"))
                .orElse(false);

        if(hasSuperclass) {
            neededImports.add(javaClass.getSuperclass());
        }

        final List<TypeScriptProperty> properties = createListOfTSPropertiesByJavaClass(javaClass);
        for (TypeScriptProperty property : properties) {
            property.getNeededImports().stream()
                    .filter(i -> neededImports.stream().noneMatch(ne -> ne.getName().equals(i.getName())))
                    .forEach(neededImports::add);
        }

        final String extendsExpression = hasSuperclass ? "extends " + getTsInFileSuperClassDeclarationByChildClass(javaClass) : "";

        final StringBuilder builder = new StringBuilder();

        builder.append(String.format("export interface %s %s {", getTsInFileDeclarationByJavaClass(javaClass), extendsExpression )).append("\n");

        for(int i = 0; i < properties.size(); i++) {
            final TypeScriptProperty typeScriptProperty = properties.get(i);
            if(typeScriptProperty.getJavaType().equals(org.slf4j.Logger.class) || typeScriptProperty.getJavaType().equals(I18N.class))
                continue;

            builder.append("\t").append(typeScriptProperty.getTypeScriptDeclaration());
            if(i < properties.size() - 1) {
                builder.append(",");
            }

            builder.append("\n");
        }

        builder.append("}");

        return builder.toString();

    }

    private boolean isCoreClass(Class<?> javaClass) {
        return javaClass.getPackage().getName().startsWith("com.webharmony.core");
    }

    private List<TypeScriptProperty> createListOfTSPropertiesByJavaClass(Class<?> javaClass) {
        final List<TypeScriptProperty> propertiesByFields = Arrays.stream(javaClass.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .map(this::createTypeScriptPropertyByJavaField)
                .toList();

        final List<TypeScriptProperty> propertiesByMethods =  Arrays.stream(javaClass.getDeclaredMethods())
                .filter(method -> !Modifier.isStatic(method.getModifiers()))
                .filter(method -> method.getAnnotation(JsonProperty.class) != null)
                .map(this::createTypeScriptPropertyByJavaMethod)
                .filter(prop -> propertiesByFields.stream().noneMatch(field -> field.getName().equals(prop.getName())))
                .toList();

        final List<TypeScriptProperty> resultList = new ArrayList<>();
        resultList.addAll(propertiesByFields);
        resultList.addAll(propertiesByMethods);

        return resultList;
    }

    private TypeScriptProperty createTypeScriptPropertyByJavaField(Field field) {
        final String fieldName = Optional.ofNullable(field.getAnnotation(JsonProperty.class))
                .map(JsonProperty::value)
                .orElseGet(field::getName);
        return TypeScriptProperty.of(field.getGenericType(), fieldName);
    }

    private TypeScriptProperty createTypeScriptPropertyByJavaMethod(Method method) {
        final JsonProperty annotation = method.getAnnotation(JsonProperty.class);
        final String fieldName = Optional.ofNullable(annotation.value())
                .orElseGet(method::getName);
        return TypeScriptProperty.of(method.getGenericReturnType(), fieldName);
    }

    public static String getTsInFileSuperClassDeclarationByChildClass(Class<?> childClass) {

        final Class<?> superClass = childClass.getSuperclass();

        final String rawName = getTsClassNameByJavaClass(superClass);
        final Type genericSuperclass = childClass.getGenericSuperclass();
        if(genericSuperclass instanceof ParameterizedType parameterizedType) {
            final String suffix = "<" + Stream.of(parameterizedType.getActualTypeArguments()).map(Type::getTypeName).collect(Collectors.joining(", ")) + ">";
            return rawName + suffix;
        } else {
            return rawName;
        }
    }

    public static String getTsInFileDeclarationByJavaClass(Class<?> javaClass) {

        final String rawName = getTsClassNameByJavaClass(javaClass);
        @SuppressWarnings("all")
        final TypeVariable<? extends Class<?>>[] typeParameters = javaClass.getTypeParameters();
        if(typeParameters.length == 0) {
            return rawName;
        } else {
            final String suffix = "<" + Stream.of(typeParameters).map(TypeVariable::getName).collect(Collectors.joining(", ")) + ">";
            return rawName + suffix;
        }
    }

    public static String getTsClassNameByJavaClass(Class<?> javaClass) {
        return javaClass.getSimpleName();
    }

    public Path getPathToLocalFeProjectLibFile() {
        return Path.of(loadLocalDevProperties().getFrontendProjectDevPath()).resolve("ProjectApi.ts");
    }

    public Path getPathToLocalFeProjectConfigurationFile() {
        return Path.of(loadLocalDevProperties().getFrontendProjectDevPath()).resolve("project-configuration.json");
    }

    public Path getPathToLocalFeCoreLibFile() {
        return Path.of(loadLocalDevProperties().getFrontendCoreDevPath()).resolve("CoreApi.ts");
    }

    private LocalDevProperties loadLocalDevProperties() {
        return DevUtils.loadLocalDevProperties().orElseThrow(() -> new InternalServerException("Could not load dev properties"));
    }

    @Getter
    @AllArgsConstructor
    private static class TypeScriptProperty {

        private Type javaType;
        private String name;

        public static TypeScriptProperty of(Type javaType, String name) {
            return new TypeScriptProperty(javaType, name);
        }

        public List<Class<?>> getNeededImports() {
            return getNeededImports(this.javaType);
        }

        @SuppressWarnings("all")
        private List<Class<?>> getNeededImports(Type type) {
            // usaly only one entry
            final List<Class<?>> resultList = new ArrayList<>();
            if(type instanceof ParameterizedType parameterizedType) {
                resultList.addAll(getNeededImports(parameterizedType.getRawType()));
                for (Type actualTypeArgument : parameterizedType.getActualTypeArguments()) {
                    getNeededImports(actualTypeArgument).stream()
                            .filter(t -> resultList.stream().noneMatch(rlt -> rlt.getName().equals(t.getName())))
                            .forEach(resultList::add);
                }
            } else if(!isTsBaseType(type) && !(type instanceof WildcardType) && !(type instanceof TypeVariable<?>)) {
                resultList.add((Class<?>) type);
            }

            return resultList;
        }

        private boolean isTsBaseType(Type type) {
            final Class<?> classToCheck;
            if(type instanceof ParameterizedType parameterizedType) {
                classToCheck = (Class<?>) parameterizedType.getRawType();
            } else if (type instanceof WildcardType) {
                return false;
            } else if(type instanceof TypeVariable<?>) {
                return false;
            } else {
                classToCheck = (Class<?>) type;
            }
            return Optional.ofNullable(classToCheck.getPackage())
                    .map(Package::getName)
                    .map(pn -> !pn.startsWith("com.webharmony"))
                    .orElse(true);
        }

        public String getTypeScriptDeclaration() {
            return String.format("%s?: %s", this.getName(), getTypeScriptType(this.javaType));
        }

        private String getTypeScriptType(Type type) {
            return ApiLibraryBuilder.getTypeScriptType(type);
        }
    }

    @SuppressWarnings("all")
    public static String getTypeScriptType(Type type) {


        if(type instanceof ParameterizedType parameterizedType) {
            final Type rawType = parameterizedType.getRawType();
            if(ResponseEntity.class.equals(rawType)) {
                return getTypeScriptType(parameterizedType.getActualTypeArguments()[0]);
            }else if(Collection.class.isAssignableFrom((Class<?>) rawType)) {
                return getTypeScriptType(parameterizedType.getActualTypeArguments()[0]) + "[]";
            } else {
                final List<String> genericTypes = Stream.of(parameterizedType.getActualTypeArguments())
                        .map(ApiLibraryBuilder::getTypeScriptType).toList();

                if(rawType.getTypeName().startsWith("com.webharmony") || rawType.getTypeName().startsWith("java.util.Map")) {
                    return String.format("%s<%s>", getTypeScriptType(rawType), String.join(", ", genericTypes));
                } else {
                    return getTypeScriptType(rawType);
                }
            }
        } else if (type instanceof Class<?> clazz) {
            if(clazz.getPackage() != null && clazz.getPackage().getName().startsWith("com.webharmony")) {
                return ApiLibraryBuilder.getTsClassNameByJavaClass(clazz);
            } else if(clazz.equals(String.class)) {
                return "String";
            } else if(clazz.equals(Boolean.class) || clazz.getName().equals("boolean")) {
                return "Boolean";
            } else if(clazz.equals(Integer.class) || clazz.getName().equals("int")) {
                return "Number";
            } else if(clazz.equals(Long.class) || clazz.getName().equals("long") || clazz.equals(Double.class)) {
                return "Number";
            } else if(clazz.equals(Object.class)) {
                return "any";
            } else if(clazz.equals(Map.class)) {
                return "Map";
            } else if(clazz.equals(Class.class)) {
                return "String";
            } else if(clazz.equals(Void.class) || clazz.equals(MultipartFile.class)) {
                return "any";
            } else if(clazz.equals(UUID.class)) {
                return "String";
            } else if(clazz.equals(LocalDate.class) || clazz.equals(LocalDateTime.class)) {
                return "String";
            }  else if(clazz.isEnum()) {
                if(clazz.equals(RequestMethod.class)) {
                    return "String";
                }
                return clazz.getSimpleName();
            }  else {
                throw new IllegalArgumentException(String.format("Type %s is not supported", clazz.getName()));
            }
        } else if(type instanceof TypeVariable<?> typeVariable){
            return typeVariable.getName();
        } else {
            return "any";
        }
    }
}
