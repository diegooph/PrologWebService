package br.com.zalf.prolog.webservice.frota.pneu.transferencia;

import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.listagem.PneuTransferenciaListagem;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.visualizacao.PneuTransferenciaProcessoVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Created on 07/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface PneuTransferenciaDao {

    @NotNull
    Long insertTransferencia(@NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao,
                             @NotNull final OffsetDateTime dataHoraSincronizacao,
                             final boolean isTransferenciaFromVeiculo) throws Throwable;

    @NotNull
    Long insertTransferencia(@NotNull final Connection conn,
                             @NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao,
                             @NotNull final OffsetDateTime dataHoraSincronizacao,
                             final boolean isTransferenciaFromVeiculo) throws Throwable;

    @NotNull
    List<PneuTransferenciaListagem> getListagem(@NotNull final List<Long> codUnidadesOrigem,
                                                @NotNull final List<Long> codUnidadesDestino,
                                                @NotNull final LocalDate dataInicial,
                                                @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    PneuTransferenciaProcessoVisualizacao getVisualizacao(@NotNull final Long codTransferencia) throws Throwable;
}