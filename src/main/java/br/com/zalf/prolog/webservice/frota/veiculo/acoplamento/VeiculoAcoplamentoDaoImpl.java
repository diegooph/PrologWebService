package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento;

import br.com.zalf.prolog.webservice.commons.util.StatementUtils;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.database.DatabaseUtils;
import br.com.zalf.prolog.webservice.errorhandling.Exceptions;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamento;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Created on 2020-11-03
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class VeiculoAcoplamentoDaoImpl {
    private static final int EXECUTE_BATCH_SUCCESS = 0;

    public void removeAcoplamentoAtual(@NotNull final Connection conn,
                                       @NotNull final Long codProcessoAcoplamento) {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("delete from veiculo_acoplamento_atual where cod_processo = ?;");
            DatabaseUtils.bind(stmt, Lists.newArrayList(codProcessoAcoplamento));
            stmt.execute();
        } catch (final SQLException e) {
            throw Exceptions.rethrow(e);
        } finally {
            DatabaseConnection.close(stmt);
        }
    }

    @NotNull
    public Long insertProcessoAcoplamento(@NotNull final Connection conn,
                                          @NotNull final Long codUnidadeAcoplamento,
                                          @NotNull final Long codColaboradorRealizacao,
                                          @NotNull final OffsetDateTime dataHoraAtual,
                                          @Nullable final String observacao) {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("insert into public.veiculo_acoplamento_processo " +
                    "(cod_unidade, cod_colaborador, data_hora, observacao) " +
                    "values (?, ?, ?, ?) returning codigo as codigo;");
            DatabaseUtils.bind(stmt, Lists.newArrayList(
                    codUnidadeAcoplamento,
                    codColaboradorRealizacao,
                    dataHoraAtual,
                    StringUtils.trimToNull(observacao)));
            rSet = stmt.executeQuery();
            if (rSet.next() && rSet.getLong("codigo") > 0) {
                return rSet.getLong("codigo");
            } else {
                throw new IllegalStateException("Erro ao inserir processo de acoplamento");
            }
        } catch (final SQLException e) {
            throw Exceptions.rethrow(e);
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }

    public void insertHistoricoAcoplamentos(@NotNull final Connection conn,
                                            @NotNull final Long codProcessoAcoplamento,
                                            @NotNull final List<VeiculoAcoplamento> acoplamentos) {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("insert into public.veiculo_acoplamento_historico " +
                    "(cod_processo, cod_posicao, cod_diagrama, motorizado, cod_veiculo, km, acao) " +
                    "values (?, ?, ?, ?, ?, ?, ?);");
            for (final VeiculoAcoplamento acoplamento : acoplamentos) {
                DatabaseUtils.bind(stmt, Lists.newArrayList(
                        codProcessoAcoplamento,
                        acoplamento.getPosicaoAcaoRealizada(),
                        acoplamento.getCodDiagramaVeiculo(),
                        acoplamento.getMotorizado(),
                        acoplamento.getCodVeiculo(),
                        acoplamento.getKmColetado(),
                        acoplamento.getAcaoRealizada()));
                stmt.addBatch();
            }
            StatementUtils.executeBatchAndValidate(
                    stmt,
                    acoplamentos.size(),
                    EXECUTE_BATCH_SUCCESS,
                    "Erro ao inserir histórico de acoplamentos");
        } catch (final SQLException e) {
            throw Exceptions.rethrow(e);
        } finally {
            DatabaseConnection.close(stmt);
        }
    }

    public void insertEstadoAtualAcoplamentos(@NotNull final Connection conn,
                                              @NotNull final Long codProcessoAcoplamento,
                                              @NotNull final Long codUnidadeAcoplamento,
                                              @NotNull final List<VeiculoAcoplamento> acoplamentos) {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("insert into public.veiculo_acoplamento_atual " +
                    "(cod_processo, cod_unidade, cod_posicao, cod_diagrama, motorizado, cod_veiculo) " +
                    "values (?, ?, ?, ?, ?, ?);");
            for (final VeiculoAcoplamento acoplamento : acoplamentos) {
                DatabaseUtils.bind(stmt, Lists.newArrayList(
                        codProcessoAcoplamento,
                        codUnidadeAcoplamento,
                        acoplamento.getPosicaoAcaoRealizada(),
                        acoplamento.getCodDiagramaVeiculo(),
                        acoplamento.getMotorizado(),
                        acoplamento.getCodVeiculo()));
                stmt.addBatch();
            }
            StatementUtils.executeBatchAndValidate(
                    stmt,
                    acoplamentos.size(),
                    EXECUTE_BATCH_SUCCESS,
                    "Erro ao inserir histórico de acoplamentos");
        } catch (final SQLException e) {
            throw Exceptions.rethrow(e);
        } finally {
            DatabaseConnection.close(stmt);
        }
    }
}
