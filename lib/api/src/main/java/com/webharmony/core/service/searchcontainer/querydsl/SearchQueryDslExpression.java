package com.webharmony.core.service.searchcontainer.querydsl;

import com.querydsl.core.types.Expression;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Getter
@Setter
public class SearchQueryDslExpression<T, D> {

    private final Expression<T> queryDslExpression;
    private final String name;

    private final List<Function<?, ?>> mapperChain;


    private SearchQueryDslExpression(String label, Expression<T> queryDslExpression, List<Function<?, ?>> mapperChain) {
        this.queryDslExpression = queryDslExpression;
        this.name = label;
        this.mapperChain = mapperChain;
    }

    public static <T> SearchQueryDslExpression<T, T> of(String label, Expression<T> queryDslExpression) {
        return new SearchQueryDslExpression<>(label, queryDslExpression, new ArrayList<>());
    }

    public <R> SearchQueryDslExpression<T, R> map(Function<D, R> mapper) {
        final List<Function<?, ?>> newMapperChain = new ArrayList<>(this.mapperChain);
        newMapperChain.add(mapper);
        return new SearchQueryDslExpression<>(this.name, this.queryDslExpression, newMapperChain);
    }

    public Object executeMapperChainForInput(Object input) {
        Object result = input;
        for (Function<?, ?> function : this.mapperChain) {
            @SuppressWarnings({"unchecked", "java:S4276"})
            Function<Object, Object> objectFunction = (Function<Object, Object>) function;
            result = objectFunction.apply(result);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchQueryDslExpression<?, ?> that = (SearchQueryDslExpression<?, ?>) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
