package com.webharmony.core.service.searchcontainer.postgresql;

import com.webharmony.core.AbstractBaseTest;
import com.webharmony.core.data.jpa.model.user.AppUser;
import com.webharmony.core.utils.tuple.Tuple2;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NativeQueryBuilderTest extends AbstractBaseTest {

    @Test
    void testNativeQueryBuilder() {

        NativeQueryBuilder builder = new NativeQueryBuilder();
        builder.select("testColumn")
                .from("testTable")
                .leftJoin("simpleJoin")
                .leftJoin(PreparedSQLText.of("joinWithPlaceholder?", "p1"))
                .where("simpleWhere")
                .groupBy("g1", "g2");

        Tuple2<String, Object[]> queryData = builder.build();
        assertThat(queryData.getType1()).isEqualTo("SELECT testColumn FROM testTable LEFT JOIN simpleJoin LEFT JOIN joinWithPlaceholder? WHERE simpleWhere GROUP BY g1, g2");
        assertThat(queryData.getType2()).contains("p1");

    }

    @Test
    void shouldReturnEqualsTrueForNativeSelectExpression() {

        NativeSelectExpression nativeSelectExpression = NativeSelectExpression.of("label1", "expression1");
        NativeSelectExpression nativeSelectExpression2 = NativeSelectExpression.of("label1", "expression2");

        boolean comparisonResult1 = nativeSelectExpression.equals(nativeSelectExpression2);
        assertThat(comparisonResult1).isTrue();

        nativeSelectExpression2 = nativeSelectExpression;
        assertThat(nativeSelectExpression).isEqualTo(nativeSelectExpression2);
    }

    @Test
    @SuppressWarnings("all")
    void shouldReturnEqualsFalseForNativeSelectExpression() {

        NativeSelectExpression nativeSelectExpression = NativeSelectExpression.of("label1", "expression1");
        NativeSelectExpression nativeSelectExpression2 = null;

        boolean comparisonResult1 = nativeSelectExpression.equals(nativeSelectExpression2);
        assertThat(comparisonResult1).isFalse();

    }

    @Test
    void shouldGenerateRightAttributesForNativeTableVar() {

        NativeTableVar<AppUser> var1 = new NativeTableVar<>("app_user");
        String emailPath = var1.resolveAttributePath("email");
        assertThat(emailPath).isEqualTo("appuser.email");

        NativeTableVar<AppUser> var2 = new NativeTableVar<>(AppUser.class, "customVar");
        String emailPath2 = var2.resolveAttributePath("email");
        assertThat(emailPath2).isEqualTo("customVar.email");

        NativeTableVar<AppUser> var3 = new NativeTableVar<>("app_user", "customVar");
        String emailPath3 = var3.resolveAttributePath("email");
        assertThat(emailPath3).isEqualTo("customVar.email");

        NativeTableVar<AppUser> var4 = new NativeTableVar<>(AppUser.class, "customVar");
        String accessConfigPath = var4.resolveAttributePath(AppUser::getUserAccessConfig);
        assertThat(accessConfigPath).isEqualTo("customVar.user_access_config");


    }


}
