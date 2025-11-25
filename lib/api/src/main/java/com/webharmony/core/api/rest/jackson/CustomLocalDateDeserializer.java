package com.webharmony.core.api.rest.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("unused")
public class CustomLocalDateDeserializer extends StdDeserializer<LocalDate> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CustomLocalDateDeserializer() {
        super(LocalDate.class);
    }

    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return parseToLocalDate(jsonParser.getText());
    }

    public static LocalDate parseToLocalDate(String dateString) {
        if(dateString == null)
            return null;

        return LocalDate.parse(dateString, formatter);
    }

}
