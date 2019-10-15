package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created on 2019-10-03
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PergundaEdicaoSerializerDeserializer implements
        JsonSerializer<PerguntaModeloChecklistEdicao>,
        JsonDeserializer<PerguntaModeloChecklistEdicao> {

    @Override
    public JsonElement serialize(final PerguntaModeloChecklistEdicao src,
                                 final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        if (src instanceof PerguntaModeloChecklistEdicaoAtualiza) {
            return context.serialize(src, PerguntaModeloChecklistEdicaoAtualiza.class);
        } else if (src instanceof PerguntaModeloChecklistEdicaoInsere) {
            return context.serialize(src, PerguntaModeloChecklistEdicaoInsere.class);
        } else {
            throw new IllegalStateException("Erro ao deserializar pergunta");
        }
    }

    @Override
    public PerguntaModeloChecklistEdicao deserialize(
            final JsonElement json,
            final Type typeOfT,
            final JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        final JsonElement codigo = jsonObject.get("codigo");
        final JsonElement codigoContexto = jsonObject.get("codigoContexto");
        if (codigo != null && codigoContexto != null) {
            return context.deserialize(json, PerguntaModeloChecklistEdicaoAtualiza.class);
        } else if (codigo == null && codigoContexto == null) {
            return context.deserialize(json, PerguntaModeloChecklistEdicaoInsere.class);
        } else {
            throw new IllegalStateException(
                    String.format("Erro ao deserializar pergunta\ncodigo = %d; codigoContexto = %d;",
                            codigo != null ? codigo.getAsLong() : null,
                            codigoContexto != null ? codigoContexto.getAsLong() : null));
        }
    }
}