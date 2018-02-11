package br.com.zalf.prolog.webservice.commons.gson;

import br.com.zalf.prolog.webservice.dashboard.Color;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created on 2/5/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class ColorSerializer implements JsonSerializer<Color> {

    @Override
    public JsonElement serialize(Color color, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(color.getHex());
    }
}