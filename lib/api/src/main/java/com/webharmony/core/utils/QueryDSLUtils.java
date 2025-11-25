package com.webharmony.core.utils;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.data.jpa.model.i18n.QAppI18nEntityAttribute;
import com.webharmony.core.data.jpa.model.i18n.QAppI18nEntityAttributeValue;
import com.webharmony.core.i18n.EI18nLanguage;
import jakarta.persistence.EntityManager;

import java.util.UUID;
import java.util.function.Function;

public class QueryDSLUtils {

    private QueryDSLUtils() {

    }

    public static void initI18nAttributeJoinExpressions(JPAQuery<?> query, QAppI18nEntityAttribute path, EI18nLanguage language) {

        final QAppI18nEntityAttribute qAttribute = buildI18nAttributeVar(path);
        final QAppI18nEntityAttributeValue qAttributeValue = buildI18nAttributeValueVar(path);

        query.leftJoin(path, qAttribute);
        query.leftJoin(qAttribute.values, qAttributeValue).on(qAttributeValue.entityAttribute.uuid.eq(qAttribute.uuid).and(qAttributeValue.language.eq(language)));

    }

    private static QAppI18nEntityAttribute buildI18nAttributeVar(QAppI18nEntityAttribute path) {
        return new QAppI18nEntityAttribute(buildUniqueVarNameByPath("attribute", path));
    }

    public static QAppI18nEntityAttributeValue buildI18nAttributeValueVar(QAppI18nEntityAttribute path) {
        return new QAppI18nEntityAttributeValue(buildUniqueVarNameByPath("attributeValue", path));
    }

    public static StringPath buildI18nAttributeValueVarValue(QAppI18nEntityAttribute path) {
        return buildI18nAttributeValueVar(path).translation;
    }

    public static <E, T, P extends EntityPathBase<E>> boolean checkIsAttributeUnique(EntityManager em, P entityPathBase, Function<P, ComparablePath<UUID>> uuidPathResolver, Function<P, SimpleExpression<T>> uniqueAttributePathResolver, T attribute, UUID ownUUID) {

        final ComparablePath<UUID> uuidPath = uuidPathResolver.apply(entityPathBase);
        final SimpleExpression<T> uniqueAttributePath = uniqueAttributePathResolver.apply(entityPathBase);

        final JPAQuery<Tuple> tupleQuery = new JPAQuery<>(em)
                .from(entityPathBase)
                .select(uuidPath, uniqueAttributePath)
                .where(uniqueAttributePath.eq(attribute));

        if(ownUUID != null)
            tupleQuery.where(uuidPath.ne(ownUUID));

        return tupleQuery.fetch().isEmpty();
    }

    private static String buildUniqueVarNameByPath(String prefix, DslExpression<?> path) {
        return prefix + StringUtils.firstLetterToUpperCase(path.toString().replace(".", ""));
    }

}
