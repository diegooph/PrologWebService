package br.com.zalf.prolog.webservice.v3.frota.afericao.converter;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created on 2021-05-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Converter(autoApply = true)
public class FormaColetaDadosConverter implements AttributeConverter<FormaColetaDadosAfericaoEnum, String> {
    @Override
    public String convertToDatabaseColumn(final FormaColetaDadosAfericaoEnum formaColetaDados) {
        if (formaColetaDados == null) {
            return null;
        }
        return formaColetaDados.toString();
    }

    @Override
    public FormaColetaDadosAfericaoEnum convertToEntityAttribute(final String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        return FormaColetaDadosAfericaoEnum.fromString(s);
    }
}
