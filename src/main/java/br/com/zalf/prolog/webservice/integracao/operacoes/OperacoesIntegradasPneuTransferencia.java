package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.realizacao.PneuTransferenciaRealizacao;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

/**
 * Created on 17/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface OperacoesIntegradasPneuTransferencia {
    @NotNull
    Long insertTransferencia(@NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao,
                             @NotNull final OffsetDateTime dataHoraSincronizacao,
                             final boolean isTransferenciaFromVeiculo) throws Throwable;
}
