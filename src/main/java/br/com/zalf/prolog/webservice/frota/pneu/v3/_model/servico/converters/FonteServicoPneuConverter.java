package br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.converters;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.PneuServicoEntity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Converter
public class FonteServicoPneuConverter implements AttributeConverter<PneuServicoEntity.FonteServico, String> {

    @Override
    public String convertToDatabaseColumn(final PneuServicoEntity.FonteServico fonteServico) {
        return fonteServico.getName();
    }

    @Override
    public PneuServicoEntity.FonteServico convertToEntityAttribute(final String s) {
        return PneuServicoEntity.FonteServico.fromName(s);
    }
}
