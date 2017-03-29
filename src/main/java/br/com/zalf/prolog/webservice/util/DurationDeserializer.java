package br.com.zalf.prolog.webservice.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Created by luiz on 28/03/17.
 */
public class DurationDeserializer implements JsonDeserializer<Duration> {

    @Override
    public Duration deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {

        String duration = jsonElement.getAsString();
        String[] durationArray = duration.split(":");

        if (durationArray.length == 3) {
            int horas = Integer.parseInt(durationArray[0]);
            int minutos = Integer.parseInt(durationArray[1]);
            int segundos = Integer.parseInt(durationArray[2]);

            segundos += (minutos * 60) + (horas * 60 * 60);

            return Duration.of(segundos, ChronoUnit.SECONDS);
        } else {
            throw new IllegalArgumentException("Duration mal formatado");
        }
    }
}