package com.webharmony.core.service.searchcontainer.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.api.rest.controller.utils.RestRequestParams;
import com.webharmony.core.api.rest.controller.utils.request.SearchRequestContext;
import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.apiobject.GeneralApiResource;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.jpa.model.utils.AbstractBaseEntity;
import com.webharmony.core.i18n.EI18nLanguage;
import com.webharmony.core.service.searchcontainer.AbstractSQLSearchContainer;
import com.webharmony.core.service.searchcontainer.utils.SearchContainerAttribute;
import com.webharmony.core.service.searchcontainer.utils.SearchResult;
import com.webharmony.core.service.searchcontainer.utils.SortOrder;
import com.webharmony.core.utils.StringUtils;
import com.webharmony.core.utils.exceptions.BadRequestException;
import com.webharmony.core.utils.exceptions.InternalServerException;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import jakarta.persistence.EntityManager;

import java.util.*;

public abstract class AbstractQueryDslContainer<B extends EntityPathBase<? extends AbstractBaseEntity>, U> extends AbstractSQLSearchContainer {

    private List<SearchQueryDslExpression<?, ?>> cachedQueryExpressions = null;

    private final B genericBasePath = createGenericBasePath();

    @Override
    protected SearchResult getSearchResultsByRequestParamsInternal(SearchRequestContext searchRequestContext) {
        List<SearchQueryDslExpression<?, ?>> selectedExpressions = getSelectedSearchExpressions(searchRequestContext);

        JPAQuery<Tuple> dataQuery = createDataQuery(searchRequestContext, selectedExpressions);
        List<Tuple> tupleResults = dataQuery.fetch();

        List<GeneralApiResource<Map<String, Object>>> data = convertDtoResults(tupleResults, selectedExpressions, searchRequestContext);

        SearchResult searchResult = new SearchResult();
        searchResult.setData(data);
        searchResult.setPrimaryKeyName(getResponsePrimaryKeyName());

        final long totalResults = createCountQuery(searchRequestContext).fetch()
                .stream()
                .mapToLong(t -> t.get(0, Long.class))
                .sum();

        searchResult.setTotalResults(totalResults);

        initMetaData(searchRequestContext, searchResult);

        return searchResult;
    }

    @SuppressWarnings("unchecked")
    private B createGenericBasePath() {
        return (B) ReflectionUtils.getGenericTypeByClassAndIndex(this.getClass(), 0)
                .map(clazz -> ReflectionUtils.createNewInstanceWithSingleAttributeConstructor(clazz, generateAttributeNameForBasePath(clazz)))
                .orElseThrow(() -> new InternalServerException("Error creating generic base path for search container"));
    }

    @SuppressWarnings("unchecked")
    private String generateAttributeNameForBasePath(Class<?> qClass) {
        final B dummyInstance = (B) ReflectionUtils.createNewInstanceWithSingleAttributeConstructor(qClass, "dummyInstance");
        final String simpleClassName = dummyInstance.getType().getSimpleName();
        return StringUtils.firstLetterToLowerCase(simpleClassName);
    }

    private List<SearchQueryDslExpression<?, ?>> getSelectedSearchExpressions(SearchRequestContext searchRequestContext) {
        final List<SearchQueryDslExpression<?, ?>> allAvailableExpressions = getAllAvailableSearchExpressions();
        final List<SearchQueryDslExpression<?, ?>> resultList = new ArrayList<>();
        for (SearchContainerAttribute a : getSelectedAttributes(searchRequestContext)) {
            SearchQueryDslExpression<?, ?> searchQueryDslExpression = allAvailableExpressions.stream().filter(e -> e.getName().equals(a.getName())).findAny().orElseThrow();
            resultList.add(searchQueryDslExpression);
        }
        return resultList;
    }

    private JPAQuery<Tuple> createCountQuery(SearchRequestContext searchRequestContext) {
        final EntityManager entityManager = searchRequestContext.getApplicationContext().getBean(EntityManager.class);

        SimpleExpression<?> countExpression = (SimpleExpression<?>) getPrimaryKeyExpression().getQueryDslExpression();

        Expression<?>[] selectExpressions = {countExpression.countDistinct()};
        JPAQuery<Tuple> query = buildJpaQuery(selectExpressions, entityManager);

        initQueryFilters(query, searchRequestContext);

        return query;
    }


    private JPAQuery<Tuple> createDataQuery(SearchRequestContext searchRequestContext, List<SearchQueryDslExpression<?, ?>> selectedExpressions) {

        final EntityManager entityManager = searchRequestContext.getApplicationContext().getBean(EntityManager.class);

        List<SearchQueryDslExpression<?, ?>> expressionsForSelectQuery = new ArrayList<>(selectedExpressions);
        if(!expressionsForSelectQuery.contains(getPrimaryKeyExpression()))
            expressionsForSelectQuery.add(getPrimaryKeyExpression());

        Expression<?>[] selectExpressions = expressionsForSelectQuery.stream()
                .map(SearchQueryDslExpression::getQueryDslExpression)
                .toArray(Expression[]::new);

        JPAQuery<Tuple> query = buildJpaQuery(selectExpressions, entityManager);

        initQueryFilters(query, searchRequestContext);
        initPageSettings(query, searchRequestContext);

        query.orderBy(createOrderSpecifiers(searchRequestContext, selectedExpressions));

        return query;
    }

    protected JPAQuery<Tuple> buildJpaQuery(Expression<?>[] selectExpressions, EntityManager entityManager) {
        return new JPAQuery<>(entityManager)
                .select(selectExpressions).distinct()
                .from(getBasePath());
    }

    private void initPageSettings(JPAQuery<?> query, SearchRequestContext searchRequestContext) {
        getLimitAndOffset(searchRequestContext).ifPresent(t -> query.limit(t.getType1()).offset(t.getType2()));
    }

    private OrderSpecifier<?>[] createOrderSpecifiers(SearchRequestContext searchRequestContext, List<SearchQueryDslExpression<?, ?>> selectedExpressions) {
        return searchRequestContext.getRestRequestParams().getSorts()
                .stream()
                .map(restSort -> createOrderSpecifierByRestSort(restSort, selectedExpressions))
                .toArray(OrderSpecifier[]::new);
    }

    private OrderSpecifier<?> createOrderSpecifierByRestSort(RestRequestParams.RestSort restSort, List<SearchQueryDslExpression<?, ?>> selectedExpressions) {
        final SearchQueryDslExpression<?, ?> sortByExpression = selectedExpressions.stream()
                .filter(e -> e.getName().equals(restSort.getName()))
                .findAny()
                .orElseThrow(() -> new BadRequestException(String.format("Sort key '%s' is not available for selected attributes", restSort.getName())));

        ComparableExpressionBase<?> comparableExpression = (ComparableExpressionBase<?>) sortByExpression.getQueryDslExpression();
        SortOrder order = restSort.getOrder();
        if(order.equals(SortOrder.ASC))
            return comparableExpression.asc();
        else if(order.equals(SortOrder.DESC))
            return comparableExpression.desc();
        else
            throw new InternalServerException(String.format("Unsupported sort order '%s'", order));
    }

    protected abstract void initQueryFilters(JPAQuery<?> query, SearchRequestContext searchRequestContext);

    protected B getBasePath() {
        return genericBasePath;
    }

    private List<SearchQueryDslExpression<?, ?>> getAllAvailableSearchExpressions() {
        if(cachedQueryExpressions == null) {
            List<SearchQueryDslExpression<?, ?>> resultList = new ArrayList<>();
            initSearchExpressions(resultList);

            SearchQueryDslExpression<?, ?> primaryKeyExpression = getPrimaryKeyExpression();
            if(!resultList.contains(primaryKeyExpression))
                resultList.add(primaryKeyExpression);

            this.cachedQueryExpressions = resultList;
        }

        return this.cachedQueryExpressions;
    }

    protected abstract void initSearchExpressions(List<SearchQueryDslExpression<?, ?>> searchExpressions);

    protected abstract SearchQueryDslExpression<U, U> getPrimaryKeyExpression();



    private List<GeneralApiResource<Map<String, Object>>> convertDtoResults(List<Tuple> tupleResults, List<SearchQueryDslExpression<?, ?>> selectedExpressions, SearchRequestContext searchRequestContext) {
        final Class<? extends AbstractResourceDto> dtoClass = getResourceDtoClassBySearchRequest(searchRequestContext);
        final SearchQueryDslExpression<?, ?> primaryKey = getPrimaryKeyExpression();
        return tupleResults.stream()
                .map(tuple -> convertTupleToApiObject(tuple, selectedExpressions, primaryKey, dtoClass))
                .toList();
    }

    private GeneralApiResource<Map<String, Object>> convertTupleToApiObject(Tuple tuple, List<SearchQueryDslExpression<?, ?>> selectedExpressions, SearchQueryDslExpression<?, ?> primaryKey, Class<? extends AbstractResourceDto> dtoClass) {
        Map<String, Object> data = new LinkedHashMap<>();
        for(SearchQueryDslExpression<?, ?> searchQueryDslExpression : selectedExpressions) {
            String name = searchQueryDslExpression.getName();
            Object value = tuple.get(searchQueryDslExpression.getQueryDslExpression());

            data.put(name, searchQueryDslExpression.executeMapperChainForInput(value));
        }

        GeneralApiResource<Map<String, Object>> apiObject = new GeneralApiResource<>();
        apiObject.setData(data);

        Object resourceId = tuple.get(primaryKey.getQueryDslExpression());
        apiObject.setPrimaryKey(resourceId);
        apiObject.setResourceLinks(createResourceLinksByResourceId(dtoClass, resourceId));

        return apiObject;
    }


    @Override
    public List<SearchContainerAttribute> getAvailableAttributes() {
        return getAllAvailableSearchExpressions().stream()
                .map(e -> new SearchContainerAttribute(e.getName()))
                .toList();
    }

    protected EI18nLanguage getContextLanguage() {
        return ContextHolder.getContext().getContextLanguage();
    }

    @Override
    protected String getResponsePrimaryKeyName() {
        return getPrimaryKeyExpression().getName();
    }

}
