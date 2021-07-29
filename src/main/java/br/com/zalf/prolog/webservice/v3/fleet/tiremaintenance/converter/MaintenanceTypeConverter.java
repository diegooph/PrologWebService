package br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance.converter;

import br.com.zalf.prolog.webservice.frota.pneu.servico._model.TipoServico;
import org.jetbrains.annotations.NotNull;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created on 2021-05-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Converter(autoApply = true)
public class MaintenanceTypeConverter implements AttributeConverter<TipoServico, String> {
    @Override
    public String convertToDatabaseColumn(@NotNull final TipoServico tipoServico) {
        return tipoServico.asString();
    }

    @Override
    public TipoServico convertToEntityAttribute(@NotNull final String s) {
        return TipoServico.fromString(s);
    }
}
