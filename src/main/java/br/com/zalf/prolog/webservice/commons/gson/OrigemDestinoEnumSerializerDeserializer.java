package br.com.zalf.prolog.webservice.commons.gson;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created on 2020-04-14
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class OrigemDestinoEnumSerializerDeserializer implements
        JsonSerializer<OrigemDestinoEnum>,
        JsonDeserializer<OrigemDestinoEnum> {

    @Override
    public OrigemDestinoEnum deserialize(final JsonElement json,
                                         final Type typeOfT,
                                         final JsonDeserializationContext context) throws JsonParseException {
        return OrigemDestinoEnum.fromString(json.getAsString());
    }

    @Override
    public JsonElement serialize(final OrigemDestinoEnum origemDestinoEnum,
                                 final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        return new JsonPrimitive(origemDestinoEnum.asString());
    }
}