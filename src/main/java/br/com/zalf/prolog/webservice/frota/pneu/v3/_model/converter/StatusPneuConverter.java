package br.com.zalf.prolog.webservice.frota.pneu.v3._model.converter;

import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import org.jetbrains.annotations.NotNull;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created on 2021-03-23
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Converter
public class StatusPneuConverter implements AttributeConverter<StatusPneu, String> {

    @Override
    @NotNull
    public String convertToDatabaseColumn(@NotNull final StatusPneu statusPneu) {
        return statusPneu.asString();
    }

    @Override
    @NotNull
    public StatusPneu convertToEntityAttribute(@NotNull final String s) {
        return StatusPneu.fromString(s);
    }
}
