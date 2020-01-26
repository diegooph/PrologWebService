package br.com.zalf.prolog.webservice.frota.pneu.movimentacao;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.motivo.Motivo;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoDao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Created on 12/14/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface MovimentacaoDao {

    @NotNull
    Long insert(@NotNull final ServicoDao servicoDao,
                @NotNull final ProcessoMovimentacao processoMovimentacao,
                @NotNull final OffsetDateTime dataHoraMovimentacao,
                final boolean fecharServicosAutomaticamente) throws Throwable;

    @NotNull
    Long insert(@NotNull final Connection conn,
                @NotNull final ServicoDao servicoDao,
                @NotNull final ProcessoMovimentacao processoMovimentacao,
                @NotNull final OffsetDateTime dataHoraMovimentacao,
                final boolean fecharServicosAutomaticamente) throws Throwable;

    @NotNull
    Long insertMotivo(@NotNull final Motivo motivo, @NotNull final Long codEmpresa) throws Throwable;

    @NotNull
    List<Motivo> getMotivos(@NotNull final Long codEmpresa, final boolean onlyAtivos) throws Throwable;

    void updateMotivoStatus(@NotNull final Long codEmpresa,
                            @NotNull final Long codMotivo,
                            @NotNull final Motivo motivo) throws Throwable;
}