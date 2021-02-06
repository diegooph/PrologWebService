package br.com.zalf.prolog.webservice.commons.gson;

import br.com.zalf.prolog.webservice.commons.util.datetime.PrologDateParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDate;

public final class LocalDateSerializer implements JsonSerializer<LocalDate> {

    @Override
    public JsonElement serialize(final LocalDate localDate, final Type type, final JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(PrologDateParser.toString(localDate));
    }
}