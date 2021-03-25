package br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.converters;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.FonteServico;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.servico.PneuServicoRealizadoEntity;
import org.jetbrains.annotations.NotNull;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Converter
public class FonteServicoPneuConverter implements AttributeConverter<FonteServico, String> {

    @Override
    @NotNull
    public String convertToDatabaseColumn(@NotNull final FonteServico fonteServico) {
        return fonteServico.getName();
    }

    @Override
    @NotNull
    public FonteServico convertToEntityAttribute(@NotNull final String s) {
        return FonteServico.fromName(s);
    }
}
