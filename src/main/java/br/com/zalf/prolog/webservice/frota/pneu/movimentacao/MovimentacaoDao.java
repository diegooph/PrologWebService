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

    Long insert(ProcessoMovimentacao processoMovimentacao,
                ServicoDao servicoDao,
                boolean fecharServicosAutomaticamente) throws SQLException, OrigemDestinoInvalidaException;

    Long insert(ProcessoMovimentacao processoMovimentacao,
                ServicoDao servicoDao,
                boolean fecharServicosAutomaticamente,
                Connection conn) throws SQLException, OrigemDestinoInvalidaException;

    Long insertMotivo(@NotNull final Motivo motivo, final long codEmpresa) throws SQLException;

    List<Motivo> getMotivos(final long codEmpresa, final boolean onlyAtivos) throws SQLException;

    void updateMotivoStatus(final long codEmpresa,
                            final long codMotivo,
                            @NotNull final Motivo motivo) throws SQLException;
}