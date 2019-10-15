package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created on 2019-10-03
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AlternativaEdicaoSerializerDeserializer implements
        JsonSerializer<AlternativaModeloChecklistEdicao>,
        JsonDeserializer<AlternativaModeloChecklistEdicao> {

    @Override
    public JsonElement serialize(final AlternativaModeloChecklistEdicao src,
                                 final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        if (src instanceof AlternativaModeloChecklistEdicaoAtualiza) {
            return context.serialize(src, AlternativaModeloChecklistEdicaoAtualiza.class);
        } else if (src instanceof AlternativaModeloChecklistEdicaoInsere) {
            return context.serialize(src, AlternativaModeloChecklistEdicaoInsere.class);
        } else {
            throw new IllegalStateException("Erro ao deserializar alternativa");
        }
    }

    @Override
    public AlternativaModeloChecklistEdicao deserialize(
            final JsonElement json,
            final Type typeOfT,
            final JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        final JsonElement codigo = jsonObject.get("codigo");
        final JsonElement codigoContexto = jsonObject.get("codigoContexto");
        if (codigo != null && codigoContexto != null) {
            return context.deserialize(json, AlternativaModeloChecklistEdicaoAtualiza.class);
        } else if (codigo == null && codigoContexto == null) {
            return context.deserialize(json, AlternativaModeloChecklistEdicaoInsere.class);
        } else {
            throw new IllegalStateException(
                    String.format("Erro ao deserializar alternativa\ncodigo = %d; codigoContexto = %d;",
                            codigo != null ? codigo.getAsLong() : null,
                            codigoContexto != null ? codigoContexto.getAsLong() : null));
        }
    }
}