package br.com.zalf.prolog.webservice.v3.fleet.servicopneu.converter;

import br.com.zalf.prolog.webservice.frota.pneu.servico._model.TipoServico;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created on 2021-05-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Converter(autoApply = true)
public class TipoServicoConverter implements AttributeConverter<TipoServico, String> {
    @Override
    public String convertToDatabaseColumn(final TipoServico tipoServico) {
        return tipoServico.asString();
    }

    @Override
    public TipoServico convertToEntityAttribute(final String s) {
        return TipoServico.fromString(s);
    }
}
