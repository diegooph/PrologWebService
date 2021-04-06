package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3.converters;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;

import javax.persistence.AttributeConverter;

/**
 * Created on 2021-02-09
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public class FormaColetaDadosConverter implements AttributeConverter<FormaColetaDadosAfericaoEnum, String> {

    @Override
    public String convertToDatabaseColumn(final FormaColetaDadosAfericaoEnum formaColetaDados) {
        if (formaColetaDados == null) {
            throw new IllegalArgumentException("Forma de coleta de dados está nulo!!");
        }
        return formaColetaDados.toString();
    }

    @Override
    public FormaColetaDadosAfericaoEnum convertToEntityAttribute(final String dbData) {
        return FormaColetaDadosAfericaoEnum.fromString(dbData);
    }
}
