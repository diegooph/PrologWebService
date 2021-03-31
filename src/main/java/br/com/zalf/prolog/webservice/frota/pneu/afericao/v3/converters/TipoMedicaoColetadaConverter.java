package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3.converters;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;

import javax.persistence.AttributeConverter;

/**
 * Created on 2021-02-09
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public class TipoMedicaoColetadaConverter implements AttributeConverter<TipoMedicaoColetadaAfericao, String> {

    @Override
    public String convertToDatabaseColumn(final TipoMedicaoColetadaAfericao tipoMedicaoColetada) {
        if (tipoMedicaoColetada == null) {
            throw new IllegalArgumentException("Tipo da medição coletada está nulo!!");
        }
        return tipoMedicaoColetada.asString();
    }

    @Override
    public TipoMedicaoColetadaAfericao convertToEntityAttribute(final String dbData) {
        return TipoMedicaoColetadaAfericao.fromString(dbData);
    }
}
