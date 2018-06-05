package br.com.zalf.prolog.webservice.frota.pneu.movimentacao;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.OrigemDestinoInvalidaException;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.motivo.Motivo;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoDao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created on 12/14/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface MovimentacaoDao {

    Long insert(@NotNull final ProcessoMovimentacao processoMovimentacao,
                @NotNull final ServicoDao servicoDao,
                boolean fecharServicosAutomaticamente) throws SQLException, OrigemDestinoInvalidaException;

    Long insert(@NotNull final Connection conn,
                @NotNull final ProcessoMovimentacao processoMovimentacao,
                @NotNull final ServicoDao servicoDao,
                final boolean fecharServicosAutomaticamente) throws SQLException, OrigemDestinoInvalidaException;

    Long insertMotivo(@NotNull final Motivo motivo, @NotNull final Long codEmpresa) throws SQLException;

    List<Motivo> getMotivos(@NotNull final Long codEmpresa, final boolean onlyAtivos) throws SQLException;

    void updateMotivoStatus(@NotNull final Long codEmpresa,
                            @NotNull final Long codMotivo,
                            @NotNull final Motivo motivo) throws SQLException;
}