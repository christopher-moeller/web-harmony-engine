package com.webharmony.core.service.searchcontainer.postgresql;

import com.webharmony.core.api.rest.controller.utils.RestRequestParams;
import com.webharmony.core.utils.tuple.Tuple2;

import java.util.*;
import java.util.stream.Stream;

public class NativeQueryBuilder {

    private enum JOIN_TYPE {
        JOIN("JOIN"),
        LEFT_JOIN("LEFT JOIN");

        private final String name;

        JOIN_TYPE(String name) {
            this.name = name;
        }
    }

    private PreparedSQLText select;
    private PreparedSQLText from;

    private final List<Tuple2<PreparedSQLText, JOIN_TYPE>> joins = new ArrayList<>();
    private final List<PreparedSQLText> wheres = new ArrayList<>();

    private final List<String> groupBys = new ArrayList<>();

    private Long limit;

    private Long offset;

    private final List<RestRequestParams.RestSort> orderBy = new ArrayList<>();

    public NativeQueryBuilder select(String... selects) {
        return this.select(Stream.of(selects).map(PreparedSQLText::of).toList().toArray(new PreparedSQLText[0]));
    }

    public NativeQueryBuilder select(PreparedSQLText... selects) {

        final List<String> mergedSqlText = new ArrayList<>();
        final List<Object> mergedValues = new ArrayList<>();
        for (PreparedSQLText preparedSQLText : selects) {
            mergedSqlText.add(preparedSQLText.getSqlText());
            mergedValues.addAll(Arrays.asList(preparedSQLText.getValues()));
        }

        this.select = PreparedSQLText.of(String.join(", ", mergedSqlText), mergedValues.toArray());
        return this;
    }

    public NativeQueryBuilder from(String from) {
        return this.from(PreparedSQLText.of(from));
    }

    public NativeQueryBuilder from(PreparedSQLText from) {
        this.from = from;
        return this;
    }

    public NativeQueryBuilder leftJoin(String leftJoin) {
        return this.leftJoin(PreparedSQLText.of(leftJoin));
    }

    public NativeQueryBuilder leftJoin(PreparedSQLText leftJoin) {
        this.joins.add(Tuple2.of(leftJoin, JOIN_TYPE.LEFT_JOIN));
        return this;
    }

    public NativeQueryBuilder where(String whereCondition) {
        return this.where(PreparedSQLText.of(whereCondition));
    }

    public NativeQueryBuilder where(PreparedSQLText whereCondition) {
        this.wheres.add(whereCondition);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public NativeQueryBuilder groupBy(String... groupBys) {
        this.groupBys.addAll(Arrays.asList(groupBys));
        return this;
    }
    @SuppressWarnings("UnusedReturnValue")
    public NativeQueryBuilder orderBy(RestRequestParams.RestSort... orderBy) {
        this.orderBy.addAll(Arrays.asList(orderBy));
        return this;
    }

    public NativeQueryBuilder limit(long limit) {
        this.limit = limit;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public NativeQueryBuilder offset(long offset) {
        this.offset = offset;
        return this;
    }

    public Tuple2<String, Object[]> build() {
        StringBuilder textBuilder = new StringBuilder();

        textBuilder.append("SELECT ").append(Objects.requireNonNull(this.select.getSqlText()));
        List<Object> values = new ArrayList<>(Arrays.asList(Objects.requireNonNull(this.select).getValues()));

        textBuilder.append(" FROM ").append(Objects.requireNonNull(this.from.getSqlText()));
        values.addAll(Arrays.asList(Objects.requireNonNull(this.from).getValues()));

        for (Tuple2<PreparedSQLText, JOIN_TYPE> join : joins) {
            textBuilder.append(String.format(" %s ", join.getType2().name))
                    .append(join.getType1().getSqlText());

            values.addAll(Arrays.asList(join.getType1().getValues()));
        }

        if(!this.wheres.isEmpty()) {
            List<String> allWhereTexts = new ArrayList<>();
            for (PreparedSQLText where : this.wheres) {
                allWhereTexts.add(where.getSqlText());
                values.addAll(Arrays.asList(where.getValues()));
            }
            textBuilder.append(" WHERE ").append(String.join(" AND ", allWhereTexts));
        }

        if(!this.groupBys.isEmpty()) {
            textBuilder.append(" GROUP BY ")
                    .append(String.join(", ", this.groupBys));
        }

        if(!this.orderBy.isEmpty()) {
            textBuilder.append(" ORDER BY");
        }
        for (RestRequestParams.RestSort restSort : this.orderBy) {
            textBuilder.append(" ")
                    .append(restSort.getName())
                    .append(" ")
                    .append(restSort.getOrder().toString());
        }

        Optional.ofNullable(limit).ifPresent(v -> textBuilder.append(" LIMIT ").append(v));
        Optional.ofNullable(offset).ifPresent(v -> textBuilder.append(" OFFSET ").append(v));

        return Tuple2.of(textBuilder.toString(), values.toArray());
    }

}
