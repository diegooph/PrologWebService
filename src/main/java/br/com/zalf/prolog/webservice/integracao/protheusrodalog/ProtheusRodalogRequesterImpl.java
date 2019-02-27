package br.com.zalf.prolog.webservice.integracao.protheusrodalog;

import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.AfericaoProtheusRodalog;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.CronogramaAfericaoProtheusRodalog;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.NovaAfericaoPlacaProtheusRodalog;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 26/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ProtheusRodalogRequesterImpl implements ProtheusRodalogRequester {
    @NotNull
    @Override
    public Long insert(@NotNull final Long codUnidade,
                       @NotNull final AfericaoProtheusRodalog afericao) throws Throwable {
        // TODO - implementar o envio dos dados para o Protheus - Rodalog
        return null;
    }

    @NotNull
    @Override
    public CronogramaAfericaoProtheusRodalog getCronogramaAfericao(@NotNull final Long codUnidade) throws Throwable {
        // TODO - implementar a busca do cronograma de aferição do Protheus - Rodalog
        return null;
    }

    @NotNull
    @Override
    public NovaAfericaoPlacaProtheusRodalog getNovaAfericaoPlaca(@NotNull final Long codUnidade,
                                                                 @NotNull final String placa,
                                                                 @NotNull final String tipoAfericao) throws Throwable {
        // TODO - implementar a busca da nova aferição do Protheus - Rodalog
        return null;
    }
}
