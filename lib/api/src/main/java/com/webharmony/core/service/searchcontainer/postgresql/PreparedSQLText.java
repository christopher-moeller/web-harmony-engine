package com.webharmony.core.service.searchcontainer.postgresql;

import lombok.Getter;

@Getter
public class PreparedSQLText {

    private final String sqlText;
    private final Object[] values;

    private PreparedSQLText(String sqlText, Object[] values) {
        this.sqlText = sqlText;
        this.values = values;
    }

    public static PreparedSQLText of(String sqlText, Object... values) {
        return new PreparedSQLText(sqlText, values);
    }
}
