package br.com.zalf.prolog.webservice.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.Duration;

/**
 * Created by Zalf on 17/09/16.
 */
public class DurationSerializer implements JsonSerializer<Duration> {

    @Override
    public JsonElement serialize(Duration duration, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(formatDuration(duration));
    }


    public static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "%d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }
}
