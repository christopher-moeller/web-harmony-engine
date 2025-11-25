package com.webharmony.core.service.searchcontainer.postgresql;

import com.webharmony.core.data.jpa.model.utils.AbstractBaseEntity;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import com.webharmony.core.utils.reflection.proxy.ProxyExecutionMethod;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Table;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

public class NativeTableVar<E extends AbstractBaseEntity> {

    private final Class<E> entityClass;
    private final String tableName;

    private final String varName;

    public NativeTableVar(String tableName) {
        this(tableName, generateVarNameByTableName(tableName));
    }

    public NativeTableVar(Class<E> entityClass) {
        this.entityClass = entityClass;
        this.tableName = generateTableNameByEntityClass(entityClass);
        this.varName = generateVarNameByTableName(this.tableName);
    }

    public NativeTableVar(Class<E> entityClass, String varName) {
        this.entityClass = entityClass;
        this.tableName = generateTableNameByEntityClass(entityClass);
        this.varName = varName;
    }

    public NativeTableVar(String tableName, String varName) {
        this.varName = varName;
        this.entityClass = null;
        this.tableName = tableName;
    }


    private static String generateVarNameByTableName(String tableName) {
        return tableName.toLowerCase().replace("_", "");
    }

    private String generateTableNameByEntityClass(Class<E> entityClass) {
        return Optional.ofNullable(AnnotationUtils.findAnnotation(entityClass, Table.class))
                .map(Table::name)
                .orElseThrow();
    }

    public String resolveAttributePath(ProxyExecutionMethod<E, ?> method) {
        Method javaMethod = ReflectionUtils.getMethodByClass(this.entityClass, method);
        final String fieldName = ReflectionUtils.getFieldNameByGetterMethod(javaMethod);
        final Field field = ReflectionUtils.getFieldByName(this.entityClass, fieldName);
        final String columnName = Optional.ofNullable(AnnotationUtils.getAnnotation(field, Column.class))
                .map(Column::name)
                .orElseGet(() -> Optional.ofNullable(AnnotationUtils.getAnnotation(field, JoinTable.class)).map(JoinTable::name)
                        .orElseGet(() -> Optional.ofNullable(AnnotationUtils.getAnnotation(field, JoinColumn.class)).map(JoinColumn::name).orElse(null)));


        return resolveAttributePath(columnName);
    }

    public String resolveAttributePath(String attributePath) {
        return this.varName + "." + attributePath;
    }

    @Override
    public String toString() {
        return this.tableName + " " + this.varName;
    }
}
