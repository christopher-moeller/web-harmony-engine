package com.webharmony.core.service.data.mapper;

import com.webharmony.core.AbstractBaseTest;
import com.webharmony.core.api.rest.model.utils.annotations.ReadOnlyAttribute;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GenericObjectMapperTest extends AbstractBaseTest {

    @Test
    @SuppressWarnings("all")
    void shouldMapAToB() {

        TestObjectA objectA = new TestObjectA();
        objectA.setString1FromParent("parent String");
        objectA.setBoolean1FromParent(true);
        objectA.setInt1FromParent(1);
        objectA.setOnlyAvailableInParentA("string only available in A");
        objectA.setReadOnlyAttribute("should not be able to map to object B");

        objectA.setString1FromChild("child String");
        objectA.setBoolean1FromChild(true);
        objectA.setInt1FromChild(2);
        objectA.setOnlyAvailableInChildA("string only available in A");
        objectA.setAttributeWithDifferentType(5);

        GenericObjectMapper<TestObjectA, TestObjectB> mapper = new GenericObjectMapper<>(TestObjectA.class, TestObjectB.class);
        TestObjectB objectB = mapper.mapAToB(objectA, MappingContext.ofEmptyRequestContext());
        assertThat(objectB).isNotNull();

        assertThat(objectB.getString1FromParent()).isEqualTo(objectA.getString1FromParent());
        assertThat(objectB.getBoolean1FromParent()).isEqualTo(objectA.getBoolean1FromParent());
        assertThat(objectB.getInt1FromParent()).isEqualTo(objectA.getInt1FromParent());
        assertThat(objectB.getOnlyAvailableInParentB()).isNull();
        assertThat(objectB.getReadOnlyAttribute()).isNull();

        assertThat(objectB.getString1FromChild()).isEqualTo(objectA.getString1FromChild());
        assertThat(objectB.getBoolean1FromChild()).isEqualTo(objectA.getBoolean1FromChild());
        assertThat(objectB.getInt1FromChild()).isEqualTo(objectA.getInt1FromChild());
        assertThat(objectB.getOnlyAvailableInChildB()).isNull();
        assertThat(objectB.getAttributeWithDifferentType()).isNull();

    }

    @Test
    @SuppressWarnings("all")
    void shouldMapBToA() {
        TestObjectB objectB = new TestObjectB();
        objectB.setString1FromParent("parent String");
        objectB.setBoolean1FromParent(true);
        objectB.setInt1FromParent(1);
        objectB.setOnlyAvailableInParentB("string only available in B");
        objectB.setReadOnlyAttribute("should be able to map to object B");

        objectB.setString1FromChild("child String");
        objectB.setBoolean1FromChild(true);
        objectB.setInt1FromChild(2);
        objectB.setOnlyAvailableInChildB("string only available in B");
        objectB.setAttributeWithDifferentType(5L);

        GenericObjectMapper<TestObjectA, TestObjectB> mapper = new GenericObjectMapper<>(TestObjectA.class, TestObjectB.class);
        TestObjectA objectA = mapper.mapBToA(objectB, MappingContext.ofEmptyRequestContext());
        assertThat(objectA).isNotNull();

        assertThat(objectA.getString1FromParent()).isEqualTo(objectB.getString1FromParent());
        assertThat(objectA.getBoolean1FromParent()).isEqualTo(objectB.getBoolean1FromParent());
        assertThat(objectA.getInt1FromParent()).isEqualTo(objectB.getInt1FromParent());
        assertThat(objectA.getOnlyAvailableInParentA()).isNull();
        assertThat(objectA.getReadOnlyAttribute()).isEqualTo(objectB.getReadOnlyAttribute());

        assertThat(objectA.getString1FromChild()).isEqualTo(objectB.getString1FromChild());
        assertThat(objectA.getBoolean1FromChild()).isEqualTo(objectB.getBoolean1FromChild());
        assertThat(objectA.getInt1FromChild()).isEqualTo(objectB.getInt1FromChild());
        assertThat(objectA.getOnlyAvailableInChildA()).isNull();
        assertThat(objectA.getAttributeWithDifferentType()).isNull();
    }


    @Getter
    @Setter
    public static abstract class ParentTestObjectA {
        private String string1FromParent;
        private Boolean boolean1FromParent;
        private int int1FromParent;
        private String onlyAvailableInParentA;
        @ReadOnlyAttribute
        private String readOnlyAttribute;
    }

    @Getter
    @Setter
    public static abstract class ParentTestObjectB {
        private String string1FromParent;
        private Boolean boolean1FromParent;
        private int int1FromParent;
        private String onlyAvailableInParentB;
        private Long attributeWithDifferentType;
        private String readOnlyAttribute;
    }

    @Getter
    @Setter
    public static class TestObjectA extends ParentTestObjectA {
        private String string1FromChild;
        private Boolean boolean1FromChild;
        private int int1FromChild;
        private String onlyAvailableInChildA;
        private Integer attributeWithDifferentType;
    }

    @Getter
    @Setter
    public static class TestObjectB extends ParentTestObjectB {
        private String string1FromChild;
        private Boolean boolean1FromChild;
        private int int1FromChild;
        private String onlyAvailableInChildB;
    }


}
