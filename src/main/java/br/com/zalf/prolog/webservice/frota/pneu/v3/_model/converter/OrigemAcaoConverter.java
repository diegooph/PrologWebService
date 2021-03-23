package br.com.zalf.prolog.webservice.frota.pneu.v3._model.converter;

import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import org.jetbrains.annotations.NotNull;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created on 2021-03-23
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Converter
public class OrigemAcaoConverter implements AttributeConverter<OrigemAcaoEnum, String> {

    @Override
    @NotNull
    public String convertToDatabaseColumn(@NotNull final OrigemAcaoEnum origemAcaoEnum) {
        return origemAcaoEnum.asString();
    }

    @Override
    @NotNull
    public OrigemAcaoEnum convertToEntityAttribute(@NotNull final String s) {
        return OrigemAcaoEnum.fromString(s);
    }
}
