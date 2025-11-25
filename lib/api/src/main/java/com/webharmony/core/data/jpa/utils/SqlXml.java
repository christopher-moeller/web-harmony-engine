package com.webharmony.core.data.jpa.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.webharmony.core.utils.exceptions.InternalServerException;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import com.webharmony.core.utils.xml.XMLUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Setter
@Getter
public abstract class SqlXml<T> implements Serializable {

    private static final String NESTED_ENTITY_TAG_NAME = "NestedEntity";
    private static final String CLASS_NAME_TAG_NAME = "ClassName";
    private static final String VALUE_TAG_NAME = "Value";

    private transient T value;

    @SneakyThrows
    public void setValueFromValueString(String valueString) {
        final Type type = ((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.value = new XmlMapper().readValue(valueString, new TypeReference<>() {
            @Override
            public Type getType() {
                return type;
            }
        });
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> SqlXml<T> createNewInstanceByWrappedString(String wrappedString) {
        Document document = XMLUtils.createDocumentByString(wrappedString);

        NodeList nodes = document.getElementsByTagName(NESTED_ENTITY_TAG_NAME);
        Element rootElement = (Element) nodes.item(0);

        Element valueClassNameElement = (Element) rootElement.getElementsByTagName(CLASS_NAME_TAG_NAME).item(0);
        String valueClassName = valueClassNameElement.getTextContent();

        Element valueElement = (Element) rootElement.getElementsByTagName(VALUE_TAG_NAME).item(0);
        String valueAsString = XMLUtils.convertElementToString(XMLUtils.getFirstChildElement(valueElement));

        SqlXml<T> newInstance = (SqlXml<T>) ReflectionUtils.createNewInstanceWithEmptyConstructor(Class.forName(valueClassName));
        newInstance.setValueFromValueString(valueAsString);
        return newInstance;
    }

    public String createXmlString() {

        Document document = XMLUtils.createNewEmptyDocument();
        Element rootElement = document.createElement(NESTED_ENTITY_TAG_NAME);
        document.appendChild(rootElement);

        Element classNameElement = document.createElement(CLASS_NAME_TAG_NAME);
        classNameElement.setTextContent(this.getClass().getName());
        rootElement.appendChild(classNameElement);

        Element valueElement = document.createElement(VALUE_TAG_NAME);
        rootElement.appendChild(valueElement);

        if(this.value != null) {
            try {
                String valueXml =  new XmlMapper().registerModule(new JavaTimeModule()).writeValueAsString(this.value);
                Node valueSubElement = document.importNode(XMLUtils.createDocumentByString(valueXml).getDocumentElement(), true);
                valueElement.appendChild(valueSubElement);
            } catch (JsonProcessingException e) {
                throw new InternalServerException(e.getMessage(), e);
            }
        }

        return XMLUtils.convertDocumentToString(document, false);
    }

    @SuppressWarnings("unchecked")
    public SqlXml<T> copy() {
        SqlXml<T> newInstance = ReflectionUtils.createNewInstanceWithEmptyConstructor(this.getClass());
        newInstance.setValue(getValue());
        return newInstance;
    }


}
