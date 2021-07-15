package br.com.zalf.prolog.webservice.v3.frota.movimentacao._model;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import org.jetbrains.annotations.NotNull;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created on 2021-04-23
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Converter(autoApply = true)
public class OrigemDestinoEnumConverter implements AttributeConverter<OrigemDestinoEnum, String> {

    @Override
    public String convertToDatabaseColumn(@NotNull final OrigemDestinoEnum origemDestinoEnum) {
        return origemDestinoEnum.asString();
    }

    @Override
    public OrigemDestinoEnum convertToEntityAttribute(@NotNull final String origemDestinoAsText) {
        return OrigemDestinoEnum.fromString(origemDestinoAsText);
    }
}