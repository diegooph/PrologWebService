package br.com.zalf.prolog.webservice.integracao.protheusrodalog;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.CronogramaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericaoPlaca;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 26/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ProtheusRodalogRequesterImpl implements ProtheusRodalogRequester {
    @NotNull
    @Override
    public Long insert(@NotNull final Long codUnidade, @NotNull final Afericao afericao) throws Throwable {
        // TODO - implementar o envio dos dados para o Protheus - Rodalog
        return null;
    }

    @NotNull
    @Override
    public CronogramaAfericao getCronogramaAfericao(@NotNull final Long codUnidade) throws Throwable {
        // TODO - implementar a busca do cronograma de aferição do Protheus - Rodalog
        return null;
    }

    @NotNull
    @Override
    public NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull final Long codUnidade,
                                                  @NotNull final String placa,
                                                  @NotNull final String tipoAfericao) throws Throwable {
        // TODO - implementar a busca da nova aferição do Protheus - Rodalog
        return null;
    }
}
