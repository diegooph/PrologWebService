package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3.converters;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoProcessoColetaAfericao;

import javax.persistence.AttributeConverter;

/**
 * Created on 2021-02-09
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public class TipoProcessoColetaConverter implements AttributeConverter<TipoProcessoColetaAfericao, String> {
    @Override
    public String convertToDatabaseColumn(final TipoProcessoColetaAfericao tipoProcessoColeta) {
        if (tipoProcessoColeta == null) {
            throw new IllegalArgumentException("Tipo de processo para coleta está nulo!!");
        }
        return tipoProcessoColeta.asString();
    }

    @Override
    public TipoProcessoColetaAfericao convertToEntityAttribute(final String dbData) {
        return TipoProcessoColetaAfericao.fromString(dbData);
    }
}
