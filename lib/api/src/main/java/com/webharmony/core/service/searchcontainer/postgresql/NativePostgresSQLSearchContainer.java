package com.webharmony.core.service.searchcontainer.postgresql;

import com.webharmony.core.api.rest.controller.utils.RestRequestParams;
import com.webharmony.core.api.rest.controller.utils.request.SearchRequestContext;
import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.apiobject.GeneralApiResource;
import com.webharmony.core.service.searchcontainer.AbstractSQLSearchContainer;
import com.webharmony.core.service.searchcontainer.utils.SearchContainerAttribute;
import com.webharmony.core.service.searchcontainer.utils.SearchResult;
import com.webharmony.core.utils.tuple.Tuple2;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.*;

public abstract class NativePostgresSQLSearchContainer extends AbstractSQLSearchContainer {

    private List<NativeSelectExpression> cachedQueryExpressions = null;
    @PersistenceContext
    private EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    protected SearchResult getSearchResultsByRequestParamsInternal(SearchRequestContext searchRequestContext) {
        NativeSelectExpression[] selectExpressions = getSelectSearchExpressions(searchRequestContext);

        Query query = createDataQuery(searchRequestContext, selectExpressions);
        List<Object[]> dataQueryResults = query.getResultList();

        List<GeneralApiResource<Map<String, Object>>> data = convertDtoResults(dataQueryResults, selectExpressions, searchRequestContext);

        SearchResult searchResult = new SearchResult();
        searchResult.setData(data);
        searchResult.setPrimaryKeyName(getResponsePrimaryKeyName());
        searchResult.setTotalResults((long) createCountQuery(searchRequestContext).getSingleResult());

        initMetaData(searchRequestContext, searchResult);

        return searchResult;
    }

    private Query createDataQuery(SearchRequestContext searchRequestContext, NativeSelectExpression[] selectExpressions) {
        NativeQueryBuilder builder = new NativeQueryBuilder();
        List<String> selectExpressionsForQuery = new ArrayList<>();

        NativeSelectExpression primaryKeyExpression = getPrimaryKeyExpression();
        selectExpressionsForQuery.add(String.format("%s AS %s", primaryKeyExpression.getSelectExpression(), primaryKeyExpression.getLabel()));

        Arrays.stream(selectExpressions).map(se -> String.format("%s AS %s", se.getSelectExpression(), se.getLabel())).forEach(selectExpressionsForQuery::add);
        builder.select(selectExpressionsForQuery.toArray(new String[0]));

        initQuery(builder, searchRequestContext);
        initPageSettings(builder, searchRequestContext);
        initSortSettings(builder, searchRequestContext);

        final Tuple2<String, Object[]> queryData = builder.build();
        Query query = em.createNativeQuery(queryData.getType1());

        Object[] values = queryData.getType2();
        for (int i = 0; i < values.length; i++) {
            query.setParameter(i+1, values[i]);
        }

        return query;
    }

    private Query createCountQuery(SearchRequestContext searchRequestContext) {
        NativeQueryBuilder builder = new NativeQueryBuilder();
        builder.select(getPrimaryKeyExpression().getSelectExpression() + " as dataId");

        initQuery(builder, searchRequestContext);

        final Tuple2<String, Object[]> queryData = builder.build();

        final String countQuery = String.format("SELECT count(dataQuery.dataId) FROM (%s) as dataQuery", queryData.getType1());
        Query query = em.createNativeQuery(countQuery);

        Object[] values = queryData.getType2();
        for (int i = 0; i < values.length; i++) {
            query.setParameter(i+1, values[i]);
        }

        return query;
    }

    private List<GeneralApiResource<Map<String, Object>>> convertDtoResults(List<Object[]> queryResults, NativeSelectExpression[] selectExpressions, SearchRequestContext searchRequestContext) {
        final Class<? extends AbstractResourceDto> dtoClass = getResourceDtoClassBySearchRequest(searchRequestContext);
        List<GeneralApiResource<Map<String, Object>>> data = new ArrayList<>();

        for(Object[] row : queryResults) {
            Map<String, Object> rowResults = new HashMap<>();
            for(int i = 0; i <selectExpressions.length; i++) {
                NativeSelectExpression selectExpression = selectExpressions[i];
                Object value = row[i+1];
                rowResults.put(selectExpression.getLabel(), value);
            }

            GeneralApiResource<Map<String, Object>> apiResource = new GeneralApiResource<>();
            apiResource.setData(rowResults);

            Object primaryKey = row[0];
            apiResource.setPrimaryKey(primaryKey);
            apiResource.setResourceLinks(createResourceLinksByResourceId(dtoClass, primaryKey));

            data.add(apiResource);
        }

        return data;
    }

    protected abstract void initQuery(NativeQueryBuilder query, SearchRequestContext searchRequestContext);

    private NativeSelectExpression[] getSelectSearchExpressions(SearchRequestContext searchRequestContext) {
        final List<NativeSelectExpression> allAvailableExpressions = getAllAvailableSearchExpressions();
        return getSelectedAttributes(searchRequestContext)
                .stream()
                .map(a -> allAvailableExpressions.stream().filter(e -> e.getLabel().equals(a.getName())).findAny().orElseThrow())
                .toArray(NativeSelectExpression[]::new);
    }

    private List<NativeSelectExpression> getAllAvailableSearchExpressions() {
        if(cachedQueryExpressions == null) {
            List<NativeSelectExpression> resultList = new ArrayList<>();
            initSearchExpressions(resultList);

            NativeSelectExpression primaryKeyExpression = getPrimaryKeyExpression();
            if(!resultList.contains(primaryKeyExpression))
                resultList.add(primaryKeyExpression);

            this.cachedQueryExpressions = resultList;
        }

        return this.cachedQueryExpressions;
    }

    protected abstract NativeSelectExpression getPrimaryKeyExpression();

    private void initPageSettings(NativeQueryBuilder queryBuilder, SearchRequestContext searchRequestContext) {
        getLimitAndOffset(searchRequestContext).ifPresent(t -> queryBuilder.limit(t.getType1()).offset(t.getType2()));
    }

    private void initSortSettings(NativeQueryBuilder queryBuilder, SearchRequestContext searchRequestContext) {
        final List<String> availableSorts = getAllAvailableSearchExpressions().stream().map(NativeSelectExpression::getLabel).toList();

        List<RestRequestParams.RestSort> activeSorts = new ArrayList<>();
        for (RestRequestParams.RestSort sort : searchRequestContext.getRestRequestParams().getSorts()) {
            if(availableSorts.contains(sort.getName()))
                activeSorts.add(sort);
        }

        queryBuilder.orderBy(activeSorts.toArray(new RestRequestParams.RestSort[0]));
    }

    @Override
    public List<SearchContainerAttribute> getAvailableAttributes() {
        return getAllAvailableSearchExpressions().stream()
                .map(e -> new SearchContainerAttribute(e.getLabel()))
                .toList();
    }

    protected abstract void initSearchExpressions(List<NativeSelectExpression> searchExpressions);
    @Override
    protected String getResponsePrimaryKeyName() {
        return getPrimaryKeyExpression().getLabel();
    }
}
