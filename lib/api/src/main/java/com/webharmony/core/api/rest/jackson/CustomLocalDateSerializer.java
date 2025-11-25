package com.webharmony.core.api.rest.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomLocalDateSerializer extends StdSerializer<LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CustomLocalDateSerializer() {
        super(LocalDate.class);
    }

    @Override
    public void serialize (LocalDate value, JsonGenerator gen, SerializerProvider arg2)
            throws IOException {
        if(value != null)
            gen.writeString(parseDateToString(value));
    }

    public static String parseDateToString(LocalDate localDate) {
        if(localDate == null)
            return null;

        return localDate.format(formatter);
    }
}
