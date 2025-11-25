package com.webharmony.core.service.searchcontainer;

import com.webharmony.core.api.rest.controller.utils.RestRequestParams;
import com.webharmony.core.api.rest.controller.utils.request.RequestContext;
import com.webharmony.core.api.rest.controller.utils.request.SearchRequestContext;
import com.webharmony.core.service.searchcontainer.utils.SearchContainerAttribute;
import com.webharmony.core.service.searchcontainer.utils.SearchFilter;
import com.webharmony.core.service.searchcontainer.utils.SearchResult;
import com.webharmony.core.utils.assertions.Assert;
import com.webharmony.core.utils.exceptions.BadRequestException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class AbstractSearchContainer {

    private static final String DEFAULT_RESPONSE_PRIMARY_KEY_NAME = "id";
    private static final Integer[] DEFAULT_PAGE_OPTIONS = { 5, 10, 20, 30, 50 };
    private static final int DEFAULT_HIGHEST_PAGE_SIZE = 50;

    @Transactional(readOnly = true)
    public SearchResult getSearchResultsByRequestParams(SearchRequestContext searchRequestContext) {
        validateSearchRequest(searchRequestContext);
        return getSearchResultsByRequestParamsInternal(searchRequestContext);
    }

    protected void validateSearchRequest(SearchRequestContext searchRequestContext) {

        if(!searchRequestContext.getRestRequestParams().getIsPaged()) {
            Assert.isTrue(isUnPagedRequestAllowed())
                    .withException(() -> new BadRequestException("Only paged request allowed for this resource"))
                    .verify();
        }

        getHighestPageSize().ifPresent(highestPageSize -> Assert.isTrue(searchRequestContext.getRestRequestParams().getSize() <= highestPageSize)
                .withException(() -> new BadRequestException(String.format("Requested page size '%s' is larger than highest allowed page size '%s'", searchRequestContext.getRestRequestParams().getSize(), highestPageSize)))
                .verify());

    }

    protected abstract SearchResult getSearchResultsByRequestParamsInternal(SearchRequestContext searchRequestContext);

    public abstract List<SearchContainerAttribute> getAvailableAttributes();

    public List<String> getAvailableSortAttributes() {
        return getAvailableAttributes()
                .stream()
                .map(SearchContainerAttribute::getName)
                .toList();
    }

    public List<String> getAvailableSortAttributesForCurrentUser(RequestContext requestContext) {
        Assert.isNotNull(requestContext).verify();
        return getAvailableSortAttributes();
    }

    public List<SearchContainerAttribute> getAvailableAttributesForCurrentUser(RequestContext requestContext) {
        Assert.isNotNull(requestContext).verify();
        return getAvailableAttributes();
    }

    protected List<SearchContainerAttribute> getSelectedAttributes(SearchRequestContext searchRequestContext) {
        List<String> attributesByRequest = searchRequestContext.getRestRequestParams().getAttributes();

        if(attributesByRequest.isEmpty())
            return getAvailableAttributes();

        return getAvailableAttributes().stream()
                .filter(attribute -> attributesByRequest.contains(attribute.getName()))
                .toList();
    }

    protected List<RestRequestParams.RestSort> getAppliedSortsOptions(SearchRequestContext searchRequestContext) {
        final List<String> searchContainerAttributeNames = getAvailableAttributesForCurrentUser(searchRequestContext)
                .stream()
                .map(SearchContainerAttribute::getName)
                .toList();

        return searchRequestContext.getRestRequestParams().getSorts()
                .stream()
                .filter(attribute -> searchContainerAttributeNames.contains(attribute.getName()))
                .toList();
    }

    protected String getResponsePrimaryKeyName() {
        return DEFAULT_RESPONSE_PRIMARY_KEY_NAME;
    }

    public boolean isUnPagedRequestAllowed() {
        return false;
    }

    public List<Integer> getSuggestedPageOptions() {
        return Arrays.asList(DEFAULT_PAGE_OPTIONS);
    }

    public Optional<Integer> getHighestPageSize() {
        return isUnPagedRequestAllowed() ? Optional.empty() : Optional.of(DEFAULT_HIGHEST_PAGE_SIZE);
    }

    public List<SearchFilter> getAvailableSearchFilter() {
        return Collections.emptyList();
    }
}
