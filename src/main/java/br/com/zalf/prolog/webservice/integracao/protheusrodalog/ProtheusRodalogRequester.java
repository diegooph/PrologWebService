package br.com.zalf.prolog.webservice.integracao.protheusrodalog;

import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.AfericaoProtheusRodalog;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.CronogramaAfericaoProtheusRodalog;
import br.com.zalf.prolog.webservice.integracao.protheusrodalog.model.NovaAfericaoPlacaProtheusRodalog;
import br.com.zalf.prolog.webservice.integracao.sistema.Requester;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 26/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ProtheusRodalogRequester extends Requester {

    @NotNull
    Long insert(@NotNull final String tokenIntegracao,
                @NotNull final Long codUnidade,
                @NotNull final AfericaoProtheusRodalog afericao) throws Throwable;

    @NotNull
    CronogramaAfericaoProtheusRodalog getCronogramaAfericao(@NotNull final String tokenIntegracao,
                                                            @NotNull final Long codUnidade) throws Throwable;

    @NotNull
    NovaAfericaoPlacaProtheusRodalog getNovaAfericaoPlaca(@NotNull final String tokenIntegracao,
                                                          @NotNull final Long codUnidade,
                                                          @NotNull final String placa,
                                                          @NotNull final String tipoAfericao) throws Throwable;
}
