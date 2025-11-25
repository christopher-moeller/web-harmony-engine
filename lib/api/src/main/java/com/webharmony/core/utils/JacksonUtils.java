package com.webharmony.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JacksonUtils {

    private JacksonUtils() {

    }

    public static XmlMapper createDefaultXmlMapper() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JavaTimeModule());
        return xmlMapper;
    }

    public static ObjectMapper createDefaultJsonMapper() {
        return new ObjectMapper();
    }
}
