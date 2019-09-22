package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created on 2019-09-22
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PerguntaEdicaoDeserializer implements JsonDeserializer<PerguntaModeloChecklistEdicao> {

    @Override
    public PerguntaModeloChecklistEdicao deserialize(
            final JsonElement json,
            final Type typeOfT,
            final JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        final JsonElement codigo = jsonObject.get("codigo");
        final JsonElement codigoFixo = jsonObject.get("codigoFixo");
        if (codigo != null && codigoFixo != null) {
            return context.deserialize(json, PerguntaModeloChecklistEdicaoAtualiza.class);
        } else if (codigo == null && codigoFixo == null) {
            return context.deserialize(json, PerguntaModeloChecklistEdicaoInsere.class);
        } else {
            throw new IllegalStateException(
                    String.format("Erro ao deserializar pergunta\ncodigo = %d; codigoFixo = %d;",
                            codigo != null ? codigo.getAsLong() : null,
                            codigoFixo != null ? codigoFixo.getAsLong() : null));
        }
    }
}
