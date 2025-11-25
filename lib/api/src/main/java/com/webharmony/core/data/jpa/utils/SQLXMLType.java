package com.webharmony.core.data.jpa.utils;

import lombok.SneakyThrows;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.*;
import java.util.Objects;

@SuppressWarnings("rawtypes")
public class SQLXMLType implements UserType<SqlXml> {

    @Override
    public int getSqlType() {
        return Types.VARCHAR;
    }

    @Override
    public Class<SqlXml> returnedClass() {
        return SqlXml.class;
    }

    @Override
    public boolean equals(SqlXml sqlXml, SqlXml j1) {
        return Objects.equals(sqlXml, j1);
    }

    @Override
    public int hashCode(SqlXml sqlXml) {
        return sqlXml.hashCode();
    }

    @Override
    @SneakyThrows
    public SqlXml nullSafeGet(ResultSet resultSet, int i, SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        SQLXML sqlxml = resultSet.getSQLXML(i);
        if(sqlxml == null)
            return null;

        String xml = sqlxml.getString();
        if(resultSet.wasNull() || xml  == null) {
            return null;
        } else {
            return SqlXml.createNewInstanceByWrappedString(xml);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, SqlXml sqlXml, int index, SharedSessionContractImplementor sharedSessionContractImplementor) throws SQLException {
        if (sqlXml == null) {
            preparedStatement.setNull(index, Types.OTHER);
        } else {
            preparedStatement.setObject(index, sqlXml.createXmlString(), Types.OTHER);
        }
    }

    @Override
    public SqlXml deepCopy(SqlXml sqlXml) {
        return sqlXml == null ? null : sqlXml.copy();
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(SqlXml sqlXml) {
        return sqlXml;
    }

    @Override
    public SqlXml assemble(Serializable serializable, Object o) {
        return (SqlXml) serializable;
    }

    @Override
    public SqlXml replace(SqlXml sqlXml, SqlXml j1, Object o) {
        return sqlXml;
    }
}
